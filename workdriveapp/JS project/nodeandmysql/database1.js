console.log("Start");

const express = require('express');
const cors = require('cors');
const mysql = require('mysql2');
const cloudinary = require('cloudinary').v2;

const app = express();
const PORT = 3000;

 
cloudinary.config({
  cloud_name: 'your-cloud-name',
  api_key: 'your-api-key',
  api_secret: 'your-api-secret',
});


app.use(cors({
  origin: ['http://localhost:5500', 'http://127.0.0.1:5500','http://127.0.0.1:5501'],
  methods: ['GET', 'POST', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type']
}));
app.use(express.json());

const corsOptions = {
  origin: (origin, callback) => {
    if (!origin || origin.startsWith('http://localhost') || origin.startsWith('http://127.0.0.1')) {
      callback(null, true);
    } else {
      callback(new Error('Not allowed by CORS'));
    }
  },
  methods: ['GET', 'POST', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type']
};

app.use(cors(corsOptions));



app.use((req, res, next) => {
  console.log(" Incoming request:", req.method, req.url);
  next();
});



const pool = mysql.createPool({
  host: "localhost",
  user: "root",
  password: "Ashika0501",
  database: "Workdrive"
}).promise();


app.get('/', (req, res) => {
  res.send('Server is alive!');
});



app.post('/api/login', async (req, res) => {
  const { username, password } = req.body;
  if (!username || !password)
    return res.status(400).json({ success: false, message: 'Username and password are required' });

  try {
    const [rows] = await pool.query(
      'SELECT * FROM Users WHERE username = ? AND password = ?',
      [username, password]
    );
    if (rows.length > 0)
      res.json({ success: true, user_id: rows[0].user_id });
    else
      res.json({ success: false, message: "Invalid credentials" });
  } catch (err) {
    res.status(500).json({ success: false, message: "Server error" });
  }
});



app.post('/api/signup', async (req, res) => {
  const { username, password } = req.body;
  if (!username || !password)
    return res.status(400).json({ success: false, message: "Username and password are required" });

  try {
    const [existing] = await pool.query('SELECT * FROM Users WHERE username = ?', [username]);
    if (existing.length > 0)
      return res.status(400).json({ success: false, message: "Username already exists" });

    await pool.query('INSERT INTO Users (username, password) VALUES (?, ?)', [username, password]);
    res.json({ success: true, message: "User registered successfully" });
  } catch (err) {
    res.status(500).json({ success: false, message: "Server error" });
  }
});


app.post('/api/folders', async (req, res) => {
  const { folder_name, owner_id, parent_id } = req.body;
  if (!folder_name || !owner_id)
    return res.status(400).json({ success: false, message: 'folder_name and owner_id are required' });

  try {
    const [result] = await pool.query(
      'INSERT INTO Folders (folder_name, owner_id, parent_id) VALUES (?, ?, ?)',
      [folder_name, owner_id, parent_id || null]
    );
    res.json({ success: true, folder_id: result.insertId });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Server error creating folder' });
  }
});


app.get('/api/folders/:userId', async (req, res) => {
  const { userId } = req.params;
  try {
    const [rows] = await pool.query('SELECT * FROM Folders WHERE owner_id = ? AND deleted = 0', [userId]);
    res.json({ success: true, folders: rows });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error fetching folders' });
  }
});



app.post('/api/files', async (req, res) => {
  const { file_name, file_type, file_path, owner_id, folder_id } = req.body;

  if (!file_name || !file_path || !owner_id)
    return res.status(400).json({ success: false, message: 'Missing required fields' });

  try {
    const [result] = await pool.query(
      'INSERT INTO Files (file_name, file_type, file_path, owner_id, folder_id) VALUES (?, ?, ?, ?, ?)',
      [file_name, file_type || null, file_path, owner_id, folder_id || null]
    );
    res.json({ success: true, file_id: result.insertId });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Failed to save file metadata' });
  }
});
app.get('/api/folder-id', async (req, res) => {
  const { path = '', user_id } = req.query;
  if (!user_id) return res.status(400).json({ success: false, message: 'user_id required' });

  try {
    if (!path) {
     
      return res.json({ success: true, folder_id: null });
    }

    const parts = path.split('/').filter(Boolean);
    let parentId = null;
    let folderId = null;

    for (const part of parts) {
      const query = parentId
        ? 'SELECT folder_id FROM Folders WHERE folder_name = ? AND owner_id = ? AND parent_id = ? AND deleted = 0'
        : 'SELECT folder_id FROM Folders WHERE folder_name = ? AND owner_id = ? AND parent_id IS NULL AND deleted = 0';

      const params = parentId ? [part, user_id, parentId] : [part, user_id];
      const [rows] = await pool.query(query, params);

      if (!rows.length) {
        return res.status(404).json({ success: false, message: `Folder "${part}" not found` });
      }

      folderId = rows[0].folder_id;
      parentId = folderId;
    }

    res.json({ success: true, folder_id: folderId });
  } catch (err) {
    console.error(err);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});


app.get('/api/files/:userId', async (req, res) => {
  const { userId } = req.params;
  try {
    const [rows] = await pool.query('SELECT * FROM Files WHERE owner_id = ? AND deleted = 0', [userId]);
    res.json({ success: true, files: rows });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error fetching files' });
  }
});


app.post('/api/trash', async (req, res) => {
    console.log("POST /api/trash body:", req.body);
    let { item_type, item_id, deleted_by } = req.body;

    if (!item_type || !item_id || !deleted_by) {
        return res.status(400).json({ success: false, message: 'Required fields missing' });
    }

 
    item_id = Number(item_id);
    deleted_by = Number(deleted_by);

    try {
      
        if (item_type === 'file') {
            const [fileExists] = await pool.query('SELECT * FROM Files WHERE file_id = ?', [item_id]);
            if (fileExists.length === 0) {
                return res.status(404).json({ success: false, message: 'File not found' });
            }
        } else if (item_type === 'folder') {
            const [folderExists] = await pool.query('SELECT * FROM Folders WHERE folder_id = ?', [item_id]);
            if (folderExists.length === 0) {
                return res.status(404).json({ success: false, message: 'Folder not found' });
            }
        } else {
            return res.status(400).json({ success: false, message: 'Invalid item_type' });
        }

     
const [result] = await pool.query(
    'INSERT INTO trash (item_type, item_id, deleted_by) VALUES (?, ?, ?)',
    [item_type, item_id, deleted_by]
);


let name;
if (item_type === 'file') {
    const [[file]] = await pool.query('SELECT file_name FROM Files WHERE file_id = ?', [item_id]);
    name = file.file_name;
} else {
    const [[folder]] = await pool.query('SELECT folder_name FROM Folders WHERE folder_id = ?', [item_id]);
    name = folder.folder_name;
}


if (item_type === 'file') {
    await pool.query('UPDATE Files SET deleted = 1 WHERE file_id = ?', [item_id]);
} else {
    await pool.query('UPDATE Folders SET deleted = 1 WHERE folder_id = ?', [item_id]);
}

const [[trashRow]] = await pool.query('SELECT * FROM trash WHERE trash_id = ?', [result.insertId]);

res.json({
    success: true,
    trash_id: result.insertId,
    name,
    deleted_at: trashRow.deleted_at
});

    } catch (error) {
        console.error("Move to trash error:", error);
        res.status(500).json({ success: false, message: 'Failed to move item to trash', error: error.message });
    }
});



app.get('/api/trash/user/:userId', async (req, res) => {
  const { userId } = req.params;
  console.log('GET /api/trash/user/:userId called with userId =', userId);
  try {
    const [rows] = await pool.query('SELECT * FROM trash WHERE deleted_by = ?', [userId]);
    console.log('Trash rows:', rows);
    res.json({ success: true, trash: rows });
  } catch (err) {
    console.error('Error fetching trash:', err);  
    res.status(500).json({ success: false, message: 'Error fetching trash', error: err.message });
  }
});


app.delete('/api/trash/:trashId', async (req, res) => {
  const { trashId } = req.params;
  const { permanent } = req.query;

  try {
    const [[trashItem]] = await pool.query('SELECT * FROM trash WHERE trash_id = ?', [trashId]);
    if (!trashItem) {
      return res.status(404).json({ success: false, message: 'trash item not found' });
    }

    const { item_type, item_id } = trashItem;

    if (permanent === 'true') {
  if (item_type === 'file') {
    await pool.query('DELETE FROM Files WHERE file_id = ?', [item_id]);
  } else if (item_type === 'folder') {
  
    await pool.query('DELETE FROM Files WHERE folder_id = ?', [item_id]);

   
    const [subfolders] = await pool.query('SELECT folder_id FROM Folders WHERE parent_id = ?', [item_id]);
    for (const sub of subfolders) {
      await pool.query('DELETE FROM Files WHERE folder_id = ?', [sub.folder_id]);
      await pool.query('DELETE FROM Folders WHERE folder_id = ?', [sub.folder_id]);
    }

   
    await pool.query('DELETE FROM Folders WHERE folder_id = ?', [item_id]);
  }
}

    else {
 
      if (item_type === 'file') {
        await pool.query('UPDATE Files SET deleted = 0 WHERE file_id = ?', [item_id]);
      } else {
        await pool.query('UPDATE Folders SET deleted = 0 WHERE folder_id = ?', [item_id]);
      }
    }

   
    await pool.query('DELETE FROM trash WHERE trash_id = ?', [trashId]);

    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Failed to delete or restore item', error: err.message });
  }
});


app.get('/api/trash/item/:trashId', async (req, res) => {
  const { trashId } = req.params;
  try {
    const [rows] = await pool.query('SELECT * FROM trash WHERE trash_id = ?', [trashId]);
    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Trash item not found' });
    }
    res.json({ success: true, trashItem: rows[0] });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error fetching trash item' });
  }
});




app.get('/api/users/username/:username', async (req, res) => {
  const { username } = req.params;
  try {
    const [rows] = await pool.query('SELECT user_id, username FROM Users WHERE username = ?', [username]);
    if (rows.length === 0)
      return res.json({ success: false, message: 'User not found' });
    res.json({ success: true, user: rows[0] });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error finding user', error: err.message });
  }
});



app.post('/api/share/file', async (req, res) => {
  const { file_id, shared_by, shared_with } = req.body;
  if (!file_id || !shared_by || !shared_with)
    return res.status(400).json({ success: false, message: 'Missing fields' });

  try {
    const [exists] = await pool.query(
      'SELECT * FROM FileShares WHERE file_id = ? AND shared_with = ?',
      [file_id, shared_with]
    );
    if (exists.length)
      return res.json({ success: false, message: 'Already shared' });

    await pool.query(
      'INSERT INTO FileShares (file_id, shared_by, shared_with) VALUES (?, ?, ?)',
      [file_id, shared_by, shared_with]
    );
    res.json({ success: true, message: 'File shared successfully' });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Failed to share file', error: err.message });
  }
});

app.post('/api/share/folder', async (req, res) => {
  console.log("Incoming folder share request:", req.body);

  
  const { folder_id, file_id, shared_by, shared_with } = req.body;
  const id = folder_id || file_id; 

  if (!id || !shared_by || !shared_with) {
    console.log("Missing fields:", req.body);
    return res.status(400).json({ success: false, message: 'Missing fields' });
  }

  try {
    const [exists] = await pool.query(
      'SELECT * FROM FolderShares WHERE file_id = ? AND shared_with = ?',
      [id, shared_with]
    );

    if (exists.length > 0) {
      console.log("Already shared!");
      return res.json({ success: false, message: 'Already shared' });
    }

    await pool.query(
      'INSERT INTO FolderShares (file_id, shared_by, shared_with) VALUES (?, ?, ?)',
      [id, shared_by, shared_with]
    );

    console.log("Folder shared successfully!");
    res.json({ success: true, message: 'Folder shared successfully' });

  } catch (err) {
    console.error("Error sharing folder:", err);
    res.status(500).json({ success: false, message: 'Failed to share folder', error: err.message });
  }
});

app.get('/api/shared/:userId', async (req, res) => {
  const { userId } = req.params;

  try {
   
    const [files] = await pool.query(`
      SELECT 
        f.file_id, 
        f.file_name, 
        f.file_path, 
        f.created_at ,
        u.username AS shared_by
      FROM FileShares s
      JOIN Files f ON s.file_id = f.file_id
      JOIN Users u ON s.shared_by = u.user_id
      WHERE s.shared_with = ? AND f.deleted = 0
    `, [userId]);

   
    const [folders] = await pool.query(`
      SELECT 
        fo.folder_id, 
        fo.folder_name, 
        fo.created_at,       
        u.username AS shared_by
      FROM FolderShares s
      JOIN Folders fo ON s.file_id = fo.folder_id
      JOIN Users u ON s.shared_by = u.user_id
      WHERE s.shared_with = ? AND fo.deleted = 0
    `, [userId]);

    res.json({ success: true, shared_files: files, shared_folders: folders });

  } catch (err) {
    console.error("Error fetching shared items:", err);
    res.status(500).json({ success: false, message: 'Error fetching shared items', error: err.message });
  }
});




app.listen(PORT, () => {
  console.log(` Server running on http://localhost:${PORT}`);
});