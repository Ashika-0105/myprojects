const user_id = localStorage.getItem("user_id");
let username = "";

// Core data
let parsedData = { files: [], folders: [], trash: [] };
let currentPath = "";
let selectedFolderPath = "";
let currentImgSrc = "";
let currentFileName = "";

// UI references (unchanged)
const sideBar = document.getElementById("sidebar");
const topMenu = document.querySelector(".top-menu");
const secondaryMenu = document.querySelector(".secondary-menu");
const imgPreview = document.getElementById("img-preview");
const modal = document.getElementById('modal');
const overlay = document.getElementById('overlay');
const cancelButton = document.getElementById('cancel');
const folderNameInput = document.getElementById('folder-name');
const createFolderBtn = document.getElementById("create-folder");
const sidebarNew = document.querySelector(".sidebar-new");
let fileListing = document.getElementById('file-listing');
let headerColumn = document.querySelector(".header-column");
let downloadBtn = document.getElementById("download-btn");

// Get username once per session
async function getUsername() {
  const res = await fetch(`/api/get-username?user_id=${user_id}`);
  const data = await res.json();
  username = data.username || "Unknown";
}

// Fetch all files, folders, trash from backend DB
async function fetchParsedData(path = "") {
  await getUsername();
  const [foldersRes, filesRes, trashRes] = await Promise.all([
    fetch(`/api/user-folders?owner_id=${user_id}`),
    fetch(`/api/user-files?owner_id=${user_id}`),
    fetch(`/api/user-trash?deleted_by=${user_id}`)
  ]);
  const foldersArr = (await foldersRes.json()).folders || [];
  const filesArr = (await filesRes.json()).files || [];
  const trashArr = (await trashRes.json()).trash || [];
  parsedData.folders = foldersArr.map(f => ({
    ...f,
    type: "folder",
    fullPath: f.parent_id ? buildFolderFullPath(f, foldersArr) : f.folder_name,
    uploadedBy: username,
    uploadedDate: toLocaleDateString(f.created_at),
  }));
  parsedData.files = filesArr.map(f => ({
    ...f,
    type: f.file_type,
    src: f.file_path,
    webkitRelativePath: getFilePathName(f, foldersArr),
    uploadedBy: username,
    uploadedDate: toLocaleDateString(f.created_at),
  }));
  parsedData.trash = trashArr.map(t => ({
    ...t,
    uploadedBy: username,
    trashedTime: toLocaleDateString(t.deleted_at),
  }));
}

// Helper fns (unchanged)
function buildFolderFullPath(folder, allFolders) {
  let path = folder.folder_name;
  let curr = folder;
  while (curr.parent_id) {
    const parent = allFolders.find(f => f.folder_id === curr.parent_id);
    if (!parent) break;
    path = parent.folder_name + '/' + path;
    curr = parent;
  }
  return path;
}
function getFilePathName(file, allFolders) {
  if (!file.folder_id) return file.file_name;
  const folder = allFolders.find(f => f.folder_id === file.folder_id);
  if (!folder) return file.file_name;
  return (folder.fullPath || folder.folder_name) + '/' + file.file_name;
}
function toLocaleDateString(dt) {
  if (!dt) return "";
  return new Date(dt).toLocaleDateString();
}


createFolderBtn.addEventListener("click", async function() {
  const folderName = folderNameInput.value.trim();
  if (folderName) {
    let parentFolder = getFolderByFullPath(selectedFolderPath || currentPath);
    let parent_id = parentFolder ? parentFolder.folder_id : null;
    await fetch('/api/create-folder', {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        folder_name: folderName,
        owner_id: user_id,
        parent_id
      })
    });
    await fetchParsedData(currentPath);
    refreshCurrentView();
    closeModal();
    selectedFolderPath = "";
  } else {
    alert("Please enter a folder name!");
  }
});


sidebarNew.addEventListener("click", function(event) {
  if (event.target.classList.contains("upload-file-btn")) {
    let inputfilebtn = document.getElementById("filepicker");
    inputfilebtn.click();
    inputfilebtn.onchange = async function () {
      for (let i = 0; i < inputfilebtn.files.length; i++) {
        let file = inputfilebtn.files[i];
        try {
          const cloudinaryUrl = await uploadToCloudinary(file);
          let parentFolder = getFolderByFullPath(selectedFolderPath || currentPath);
          let folder_id = parentFolder ? parentFolder.folder_id : null;
          await fetch('/api/upload-file', {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              file_name: file.name,
              file_type: file.type,
              file_path: cloudinaryUrl,
              owner_id: user_id,
              folder_id
            })
          });
        } catch (err) {
          alert("Upload to Cloudinary failed: " + err.message);
        }
      }
      await fetchParsedData(currentPath);
      refreshCurrentView();
      selectedFolderPath = "";
    };
  }
});

// Upload folder: backend + cloud + owner/username
sidebarNew.addEventListener("click", function(event) {
  if (event.target.classList.contains("upload-folder-btn")) {
    let inputfolderbtn = document.getElementById("folderpicker");
    inputfolderbtn.click();
    inputfolderbtn.onchange = async function () {
      const pathMap = new Map();
      for (let file of inputfolderbtn.files) {
        const fullPathArr = file.webkitRelativePath.split('/');
        fullPathArr.pop();
        let ancestor = selectedFolderPath || currentPath;
        for (let part of fullPathArr) {
          ancestor = ancestor ? ancestor + '/' + part : part;
          if (!pathMap.has(ancestor)) {
            let parentFolder = getFolderByFullPath(ancestor.split('/').slice(0, -1).join('/'));
            let parent_id = parentFolder ? parentFolder.folder_id : null;
            await fetch('/api/create-folder', {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({
                folder_name: part,
                owner_id: user_id,
                parent_id
              })
            });
            pathMap.set(ancestor, true);
          }
        }
      }
      for (let file of inputfolderbtn.files) {
        try {
          const cloudinaryUrl = await uploadToCloudinary(file);
          const pathArr = (selectedFolderPath || currentPath ? (selectedFolderPath || currentPath) + '/' : '') + file.webkitRelativePath.split('/').slice(0, -1).join('/');
          let folder = getFolderByFullPath(pathArr);
          let folder_id = folder ? folder.folder_id : null;
          await fetch('/api/upload-file', {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              file_name: file.name,
              file_type: file.type,
              file_path: cloudinaryUrl,
              owner_id: user_id,
              folder_id
            })
          });
        } catch (err) {
          alert("Upload failed for file " + file.name + ": " + err.message);
        }
      }
      await fetchParsedData(currentPath);
      refreshCurrentView();
    };
  }
});

function getFolderByFullPath(fullPath) {
  if (!fullPath) return null;
  return parsedData.folders.find(f => f.fullPath === fullPath);
}

// Cloudinary upload unchanged
async function uploadToCloudinary(file) {
  const url = "https://api.cloudinary.com/v1_1/dheizv6u0/image/upload";
  const formData = new FormData();
  formData.append('file', file);
  formData.append('upload_preset', 'Workdrive');
  const response = await fetch(url, { method: 'POST', body: formData });
  if (!response.ok) throw new Error('Cloudinary upload failed');
  const data = await response.json();
  return data.secure_url;
}

// --- TRASH section, now via DB ---
async function moveToTrash(item, type) {
  // Send to backend trash
  await fetch('/api/move-to-trash', {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      item_type: type,
      item_id: type === "folder" ? item.folder_id : item.file_id,
      deleted_by: user_id,
    })
  });
  await fetchParsedData(currentPath);
  refreshCurrentView();
}

// Restore item from trash
async function restoreItem(item, type) {
  await fetch('/api/restore-from-trash', {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      item_type: type,
      item_id: type === "folder" ? item.folder_id : item.file_id,
      deleted_by: user_id,
    })
  });
  await fetchParsedData(currentPath);
  refreshCurrentView();
}

// Delete forever
async function deleteForever(item, type) {
  await fetch('/api/delete-forever', {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      item_type: type,
      item_id: type === "folder" ? item.folder_id : item.file_id,
      deleted_by: user_id,
    })
  });
  await fetchParsedData(currentPath);
  refreshCurrentView();
}

// Rendering logic for files/folders/trash unchanged

window.onload = async function () {
  await fetchParsedData();
  refreshCurrentView();
}

// -------------- END index.js --------------