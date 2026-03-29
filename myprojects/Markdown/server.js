import express from 'express';
import cors from 'cors';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import mysql from 'mysql';

const app = express();
const PORT = 3005;

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

app.use(cors({
  origin: 'http://localhost:5173',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  optionsSuccessStatus: 200 
}));

app.use(express.json());


const db = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "Ashika0501",
    database: "markdown"
});

db.connect((err) => {
    if (err) {
        console.error("Database connection failed: " + err.stack);
        return;
    }
    console.log("Connected to database");
});
app.get('/', (req, res) => res.send("Hello world!"));

app.post('/save-file', (req, res) => {
    const { filename, content } = req.body;

    const nameToUse = filename || req.body.fileName;
    
    if (!nameToUse) return res.status(400).send("Filename is required");

    const filePath = path.join(__dirname, 'markdown', nameToUse);

    fs.writeFile(filePath, content, (err) => {
        if (err) return res.status(500).send("Cant write file");
        
        const sql = "INSERT INTO files (filename, filepath) VALUES (?, ?)";
        db.query(sql, [nameToUse, filePath], (dbErr) => {
            if (dbErr) return res.status(500).send("Error saving to DB");
            res.send({ message: "Saved to folder and database!" });
        });
    });
});

app.get('/getall-files', (req, res) => {
    const sql = "SELECT * FROM files";
    db.query(sql, (dbErr, results) => {
        if (dbErr) return res.status(500).send("Error getting files");
        res.json(results.map(row => ({ id: row.id, filename: row.filename })));
    });
});

app.get('/get-file', (req, res) => {
    const filename = req.query.name;
    const sql = "SELECT * FROM files WHERE filename = ?";
    db.query(sql, [filename], (dbErr, results) => {
        if (dbErr || results.length === 0) return res.status(404).send("File not found");
        const row = results[0];
        try {
            const content = fs.readFileSync(row.filepath, 'utf8');
            res.json({ id: row.id, filename: row.filename, content: content });
        } catch (e) { res.status(500).send("File missing on disk"); }
    });
});


app.put('/update-file', (req, res) => {
    const { originalName, filename, content } = req.body;
    
    const sqlSearch = "SELECT filepath FROM files WHERE filename = ?";
    db.query(sqlSearch, [originalName || filename], (dbErr, results) => {
        if (dbErr || results.length === 0) return res.status(404).send("File not found in database");
        
        const oldPath = results[0].filepath;
        const newPath = path.join(__dirname, 'markdown', filename);

        fs.writeFile(oldPath, content, (err) => {
            if (err) return res.status(500).send("Error writing file");
            
            if (oldPath !== newPath) {
                fs.renameSync(oldPath, newPath);
                const sqlUpdate = "UPDATE files SET filename = ?, filepath = ? WHERE filename = ?";
                db.query(sqlUpdate, [filename, newPath, originalName || filename], () => {
                    res.send({ message: "File and name updated successfully!" });
                });
            } else {
                res.send({ message: "File updated successfully!" });
            }
        });
    });
});

app.delete('/delete-file', (req, res) => {
    const filename = req.query.name;
    const sqlSelect = "SELECT filepath FROM files WHERE filename = ?";
    db.query(sqlSelect, [filename], (err, results) => {
        if (err || results.length === 0) return res.status(404).send("Not found");
        const filePath = results[0].filepath;
        fs.unlink(filePath, () => {
            db.query("DELETE FROM files WHERE filename = ?", [filename], () => {
                res.send({ message: "File deleted successfully" });
            });
        });
    });
});

app.get('/test', (req, res) => {
    res.send("Server is working");
});



app.listen(PORT, () => console.log(`Server on ${PORT}`));