let parsedData = {
    folders: [],
    files: [],
    trash: []
};

let currentItem = null;
let currentType = null;


const currentUserId = localStorage.getItem('user_id');
if (!currentUserId) {
    alert('No user logged in!');
    window.location.href = 'login.html';
}


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
const selectiondiv = document.querySelector(".selection-bar");
const itemsselectedno = document.querySelector(".number-selected");
const headerColumn = document.querySelector(".header-column");
let downloadBtn = document.getElementById("download-btn");
const foldersvg = `<svg aria-label="Folder" DataSvgName="wd_icon_folder" viewBox="0 0 24 24" id="ember627" class="">
        <path d="M4 20h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-6.333a2 2 0 0 1-1.2-.4L9.533 4.4a2 2 0 0 0-1.2-.4H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2Zm.5-11.5a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h15a1 1 0 0 0 1-1v-8a1 1 0 0 0-1-1h-15Z" fill="" opacity="" stroke="" transform="" fill-rule="evenodd" fill-opacity="" clip-rule=""></path>
        <path d="M3.5 9.5a1 1 0 0 1 1-1h15a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1h-15a1 1 0 0 1-1-1v-8Z" fill="" opacity="0.12" stroke="" transform="" fill-rule="" fill-opacity="" clip-rule=""></path>
    </svg>`;
let filePath = document.querySelector(".file-path");
let filePathName = document.querySelector(".file-path-name");    
let newButton = document.querySelector(".primary-button");
let currentImgSrc = "";
let currentFileName = "";
let selectedFolderPath = "";
let currentPath = ""; 
let currentPathParts = []; 
const shareButton = document.getElementById('share-button');
const shareDialog = document.querySelector('.share-dialog');
const closeBtn = document.querySelector('.close-btn');
const over = document.getElementById('over');

function openDialog() {
  shareDialog.classList.add('active');
  over.classList.add('active');
}

function closeDialog() {
  shareDialog.classList.remove('active');
  over.classList.remove('active');
}

shareButton.addEventListener('click', openDialog);
closeBtn.addEventListener('click', closeDialog);
over.addEventListener('click', closeDialog);


newButton.addEventListener("click", () => {
  sidebarNew.style.display = "block";
});
document.addEventListener("click", e => {
  if (!sidebarNew.contains(e.target) && !document.querySelector(".primary-button").contains(e.target)) {
    sidebarNew.style.display = "none";
  }
});
sidebarNew.addEventListener("click", e => {
  if (e.target.classList.contains("folder-btn")) {
    modal.classList.add('active');
    overlay.classList.add('active');
  }
});
cancelButton.addEventListener('click', closeModal);
function closeModal() {
  modal.classList.remove('active');
  overlay.classList.remove('active');
  folderNameInput.value = ''; 
}

const BACKEND_URL = "http://localhost:3000"; 
async function fetchUserData() {
    try {
        const [foldersRes, filesRes] = await Promise.all([
            fetch(`${BACKEND_URL}/api/folders/${currentUserId}`),
            fetch(`${BACKEND_URL}/api/files/${currentUserId}`)
        ]);

        const foldersData = await foldersRes.json();
        const filesData = await filesRes.json();

        if (foldersData.success && filesData.success) {
            
            parsedData.folders = (foldersData.folders || []).map(f => ({
                ...f,
                type: "folder",
                fullPath: f.parent_path ? `${f.parent_path}/${f.folder_name}` : f.folder_name
            }));

                parsedData.files = (filesData.files || []).map(f => ({
        ...f,
        type: "file"
    }));

        } else {
            parsedData.folders = [];
            parsedData.files = [];
            fileListing.innerHTML = '';
            alert('No files or folders found.');
        }

    } catch (err) {
        console.error(err);
        fileListing.innerHTML = '';
        alert('Error loading data from server');
    }
}




function createItem(item) {
  const itemDiv = document.createElement("div");
  itemDiv.classList.add("zwd-item");
  if (item.public_id) itemDiv.setAttribute('data-id', item.public_id);
  else if (item.fullPath) itemDiv.setAttribute('data-id', item.fullPath);
  else if (item.file_id) itemDiv.setAttribute('data-id', item.file_id);
  else if (item.folder_id) itemDiv.setAttribute('data-id', item.folder_id);

  const contentDiv = document.createElement('div');
  contentDiv.classList.add('zwd-content');

  const checkBox = document.createElement('input');
  checkBox.type = 'checkbox';
  checkBox.classList.add('custom-checkbox');
  contentDiv.appendChild(checkBox);

  const imgContent = document.createElement('div');
  imgContent.classList.add('img-content');
  if (item.type === "file" || (item.type && item.type.startsWith("image"))) {
   imgContent.innerHTML = `
  <svg aria-label="Image" DataSvgName="wd_img" viewBox="0 0 24 24"
       class="img zwd-icon-26 zwd-inline-block ember-view">
    <path 
      d="M3 5.75A2.75 2.75 0 0 1 5.75 3h12.5A2.75 2.75 0 0 1 21 5.75v12.5A2.75 2.75 0 0 1 18.25 21H5.75A2.75 2.75 0 0 1 3 18.25V5.75ZM5.75 4.5c-.69 0-1.25.56-1.25 1.25v8.357l.617-.514a2.75 2.75 0 0 1 3.705.168l2.579 2.58 3.984-5.645a2.75 2.75 0 0 1 4.115-.433V5.75c0-.69-.56-1.25-1.25-1.25H5.75Zm.327 10.245L4.5 16.06v2.19c0 .69.56 1.25 1.25 1.25h12.5c.69 0 1.25-.56 1.25-1.25v-5.69a.752.752 0 0 1-.076-.08l-.832-.998a1.25 1.25 0 0 0-1.982.079l-4.497 6.372a.75.75 0 0 1-1.143.097L7.76 14.822a1.25 1.25 0 0 0-1.684-.077ZM11.5 9a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0ZM13 9a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
      fill="#5ec295"
    />
  </svg>
`;


  }else if (item.type === "folder") {
    imgContent.innerHTML = foldersvg;
  }

  const infoContent = document.createElement('div');
  infoContent.classList.add('info-content');
  const itemName = document.createElement('span');
  itemName.classList.add('item-name');
  itemName.textContent = item.folder_name || item.file_name || '';
  infoContent.appendChild(itemName);

  const uploadedInfoDiv = document.createElement('div');
  uploadedInfoDiv.classList.add('uploaded-info');
  uploadedInfoDiv.innerHTML = `<span>Uploaded by </span><span class="title">${item.uploadedBy || 'You'}</span>`;
  infoContent.appendChild(uploadedInfoDiv);

  const modifiedDateDiv = document.createElement('div');
  modifiedDateDiv.classList.add('Modified-date');
  const dateSpan = document.createElement('span');
  dateSpan.classList.add('date');
  dateSpan.textContent = item.uploadedDate || '';
  modifiedDateDiv.appendChild(dateSpan);

  contentDiv.appendChild(imgContent);
  contentDiv.appendChild(infoContent);
  itemDiv.appendChild(contentDiv);
  itemDiv.appendChild(modifiedDateDiv);

  fileListing.appendChild(itemDiv);

  itemDiv.addEventListener("click", e => {
    if (e.target === checkBox) return;
    currentItem = item;
    currentType = item.type === "folder" ? "folder" : "file";
    
    checkBox.checked = !checkBox.checked;
    let selectedFiles = 0, selectedFolders = 0;
    document.querySelectorAll(".zwd-item").forEach(itemEl => {
      const cb = itemEl.querySelector(".custom-checkbox");
      const checked = cb && cb.checked;
      const isFolder = itemEl.querySelector(".img-content svg");
      if (checked) {
        if (isFolder) selectedFolders++;
        else selectedFiles++;
      }
    });
    const parts = [];
    if (selectedFolders) parts.push(`${selectedFolders} folder${selectedFolders > 1 ? 's' : ''}`);
    if (selectedFiles) parts.push(`${selectedFiles} file${selectedFiles > 1 ? 's' : ''}`);
    itemsselectedno.textContent = parts.length > 0 ? parts.join(", ") + " selected" : "";
    headerColumn.style.visibility = (selectedFiles + selectedFolders) > 0 ? "hidden" : "visible";
    selectiondiv.style.visibility = (selectedFiles + selectedFolders) > 0 ? "visible" : "hidden";
  });
  if (item.type === "folder") {
      item.fullPath = currentPath ? `${currentPath}/${item.folder_name}` : item.folder_name;
  }

  itemDiv.addEventListener("dblclick", () => {
    if (item.type === "folder") {
        const targetPath = item.fullPath || item.folder_name; 
        if (!targetPath) return; 
        goToFolder(targetPath);
        filePath.style.display = "flex";
    } else {
      sideBar.style.visibility = "hidden";
      topMenu.style.visibility = "hidden";
      secondaryMenu.style.visibility = "hidden";
      headerColumn.style.visibility = "hidden";
      fileListing.style.visibility = "hidden";
      imgPreview.style.visibility = "visible";

      const previewImg = imgPreview.querySelector(".image-container img");
      const imgSrc = item.file_path || item.src || item.icon || '';
      previewImg.src = imgSrc;
      currentImgSrc = imgSrc;
      currentFileName = item.file_name || '';

      const fileNameDiv = imgPreview.querySelector(".file-name");
      fileNameDiv.textContent = currentFileName;
    }
  });
}

function makeDownloadableCloudinaryUrl(originalUrl, fileName) {
  const cleanFileName = (fileName || 'download').replace(/\.[^/.]+$/, "");
  const parts = originalUrl.split("/upload/");
  if (parts.length !== 2) return originalUrl;
  const transformation = `fl_attachment:${cleanFileName}`;
  return `${parts[0]}/upload/${transformation}/${parts[1]}`;
}

downloadBtn.addEventListener("click", () => {
  if (!currentImgSrc) {
    alert("No image selected to download.");
    return;
  }
  const downloadUrl = makeDownloadableCloudinaryUrl(currentImgSrc, currentFileName);
  const tempLink = document.createElement("a");
  tempLink.href = downloadUrl;
  tempLink.setAttribute("download", currentFileName || "downloaded-file.jpg");
  document.body.appendChild(tempLink);
  tempLink.click();
  document.body.removeChild(tempLink);
});

const copyBtn = document.getElementById("copy-link-btn");
copyBtn.addEventListener("click", () => {
  if (!currentImgSrc) return alert("No image selected.");
  navigator.clipboard.writeText(currentImgSrc)
    .then(() => alert("View link copied!"))
    .catch(() => alert("Could not copy link."));
});

let topButtons = document.querySelector(".top-buttons");
topButtons.addEventListener("click", e => {
  if (e.target.closest("#esc-button")) {
    sideBar.style.visibility = "visible";
    topMenu.style.visibility = "visible";
    secondaryMenu.style.visibility = "visible";
    headerColumn.style.visibility = "visible";
    fileListing.style.visibility = "visible";
    imgPreview.style.visibility = "hidden";
  }
});






createFolderBtn.addEventListener("click", async function() {
  const folderName = folderNameInput.value.trim();
  if (!folderName) {
    alert("Please enter a folder name!");
    return;
  }

  try {
    
    await fetchUserData(); 

    
    const parent_id = await getFolderIdFromPath(currentPath);

    
    const response = await fetch(`${BACKEND_URL}/api/folders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        folder_name: folderName,
        owner_id: currentUserId,
        parent_id: parent_id || null
      })
    });

    const data = await response.json();

    if (data.success) {
      
      parsedData.folders.push({
        folder_id: data.folder_id,
        folder_name: folderName,
        owner_id: currentUserId,
        parent_id: parent_id || null,
        type: "folder",
        fullPath: currentPath ? `${currentPath}/${folderName}` : folderName
      });

      // Refresh current view to show new folder
      refreshCurrentView(currentPath);

      // Close modal and clear input
      closeModal();
    } else {
      alert(data.message || "Failed to create folder.");
    }
  } catch (error) {
    console.error("Create folder error:", error);
    alert("Server error creating folder.");
  }
});

async function uploadToCloudinary(file) {
  const url = "https://api.cloudinary.com/v1_1/dheizv6u0/image/upload";
  const formData = new FormData();
  formData.append('file', file);
  formData.append('upload_preset', 'Workdrive'); 
  const response = await fetch(url, {
    method: 'POST',
    body: formData
  });

  if (!response.ok) {
    throw new Error('Cloudinary upload failed');
  }

  const data = await response.json();
  return data.secure_url; 
}

sidebarNew.addEventListener("click", function(event) {
  if (event.target.classList.contains("upload-file-btn")) {
    let inputfilebtn = document.getElementById("filepicker");
    inputfilebtn.click();
    inputfilebtn.onchange = async function () {
      for (let i = 0; i < inputfilebtn.files.length; i++) {
        let file = inputfilebtn.files[i];

        try {
          const cloudinaryUrl = await uploadToCloudinary(file);

          
          const folder_id = await getFolderIdFromPath(selectedFolderPath || currentPath);

          const response = await fetch(`${BACKEND_URL}/api/files`, {  
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              file_name: file.name,
              file_type: file.type,
              file_path: cloudinaryUrl,
              owner_id: currentUserId,
              folder_id: folder_id || null
            })
          });

          const data = await response.json();
          if (!data.success) {
            alert("Failed to save file metadata on server.");
          }
        } catch (err) {
          alert("Upload or save failed: " + err.message);
        }
      }
      await fetchUserData();
        await refreshCurrentView(currentPath);
        selectedFolderPath = "";

    };
  }
});


function buildFolderPathMap(folders) {
  const map = {};
  folders.forEach(folder => {
    if (folder.fullPath && folder.folder_id) {
      map[folder.fullPath] = folder.folder_id;
    }
  });
  return map;
}


async function getFolderIdFromPath(path) {
  if (!path) return null; 

  try {
    const response = await fetch(`${BACKEND_URL}/api/folder-id?path=${encodeURIComponent(path)}&user_id=${currentUserId}`);

    
    const text = await response.text();
    let data;
    try {
      data = JSON.parse(text);
    } catch (e) {
      console.error("Invalid JSON from server:", text);
      return null; 
    }

    if (!data.success) {
      console.warn(`Folder not found for path "${path}"`);
      return null; 
    }

    return data.folder_id || null;

  } catch (err) {
    console.error("Failed to get folder ID:", err);
    return null;
  }
}


function buildFolderTree(folders, files, parentId = null) {
  return folders
    .filter(folder => folder.parent_id === parentId)
    .map(folder => {
      return {
        ...folder,
        subfolders: buildFolderTree(folders, files, folder.folder_id),
        files: files.filter(file => file.folder_id === folder.folder_id)
      };
    });
}
function renderFolderTree(tree, container) {
  tree.forEach(folder => {
    const folderDiv = document.createElement('div');
    folderDiv.classList.add('folder');
    folderDiv.textContent = folder.folder_name;

    
    const innerContainer = document.createElement('div');
    innerContainer.classList.add('folder-contents');

    folder.files.forEach(file => {
      const fileDiv = document.createElement('div');
      fileDiv.classList.add('file');
      fileDiv.textContent = file.file_name;
      innerContainer.appendChild(fileDiv);
    });

    
    renderFolderTree(folder.subfolders, innerContainer);

    folderDiv.appendChild(innerContainer);
    container.appendChild(folderDiv);
  });
}


sidebarNew.addEventListener("click", function(event) {
  if (event.target.classList.contains("upload-folder-btn")) {
    let inputfolderbtn = document.getElementById("folderpicker");
    inputfolderbtn.click();
    inputfolderbtn.onchange = async function () {
      const folderSet = new Set();
      for (let file of inputfolderbtn.files) {
        let basePath = selectedFolderPath || currentPath;
        const parts = file.webkitRelativePath.split("/");fetchUserData
        parts.pop();
        let path = basePath;
        for (let part of parts) {
          path = path ? path + "/" + part : part;
          folderSet.add(path);
        }
      }

      const folderPathToId = {};

     
      for (let folderPath of Array.from(folderSet).sort()) {
        const folderName = folderPath.split("/").pop();
        const parentPath = folderPath.split("/").slice(0, -1).join("/") || null;
        const parent_id = parentPath ? folderPathToId[parentPath] : null;
        try {
          const resp = await fetch(`${BACKEND_URL}/api/folders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              folder_name: folderName,
              owner_id: currentUserId,
              parent_id: parent_id
            })
          });
          const data = await resp.json();
          if(data.success) {
            folderPathToId[folderPath] = data.folder_id;
          } else {
            alert("Failed to create folder: " + folderName);
          }
        } catch(err) {
          alert("Error creating folder: " + folderName);
        }
      }

     
      for (let file of inputfolderbtn.files) {
        try {
          const cloudinaryUrl = await uploadToCloudinary(file);
          let basePath = selectedFolderPath || currentPath;
          const fileFolderPathParts = [basePath, file.webkitRelativePath].filter(Boolean).join("/").split("/");
          fileFolderPathParts.pop();
          const fileFolderPath = fileFolderPathParts.join("/");
          const folder_id = folderPathToId[fileFolderPath] || null;

          const fileResp = await fetch(`${BACKEND_URL}/api/files`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                file_name: file.name,
                file_type: file.type,
                file_path: cloudinaryUrl,
                owner_id: currentUserId,
                folder_id: folder_id
          })
        });

          const fileData = await fileResp.json();
          if(!fileData.success) alert("Failed to save file metadata for: " + file.name);
        } catch (err) {
          alert("Upload failed for file " + file.name + ": " + err.message);
        }
      }

     
    await fetchUserData();
    await refreshCurrentView(currentPath);  
    selectedFolderPath = "";
    };
  }
});



let moreActions = document.getElementById("more-actions");
let contextMenu = document.getElementById("context-menu");

moreActions.addEventListener("click", () => {
    contextMenu.style.visibility = "visible";
});

document.addEventListener("click", (event) => {
    if (!contextMenu.contains(event.target) && !moreActions.contains(event.target)) {
        contextMenu.style.visibility = "hidden";
    }
});

let displayValue1 = "";
const trashMenu = document.getElementById('trash-menu');

function toggleMainUI(show = true) {
    const displayValue = show ? "visible" : "hidden";
    sideBar.style.visibility = displayValue;
    topMenu.style.visibility = displayValue;
    secondaryMenu.style.visibility = displayValue;
    headerColumn.style.visibility = displayValue;
    fileListing.style.visibility = displayValue;
    displayValue1 = displayValue;
}

let currentFolders = [];
let currentFiles = [];

async function goToFolder(folderPath) {
    const folderId = await getFolderIdFromPath(folderPath);
    if (folderId !== null) {
        currentPath = folderPath;
        currentPathParts = folderPath.split('/');
        refreshCurrentView(currentPath); 
    } else {
        console.warn(`Folder "${folderPath}" not found, going to root`);
        currentPath = "";
        currentPathParts = [];
        refreshCurrentView(currentPath);
    }
}


async function refreshCurrentView(path) {
    fileListing.innerHTML = "";

    const currentFolderId = await getFolderIdFromPath(path);

    
    const foldersToShow = parsedData.folders.filter(f => f.parent_id === currentFolderId);

    
    const filesToShow = parsedData.files.filter(f => f.folder_id === currentFolderId);
    currentFiles = filesToShow;
    currentFolders = foldersToShow;
    foldersToShow.forEach(f => createItem(f));
    filesToShow.forEach(f => createItem(f));

    renderBreadcrumb(path);
}


function renderBreadcrumb(path) {
    filePathName.innerHTML = ""; 

    if (!path) {
        const span = document.createElement("span");
        span.textContent = "Home";
        filePathName.appendChild(span);
        return;
    }

    const parts = path.split("/");
    let accumulatedPath = "";

    parts.forEach((part, index) => {
        accumulatedPath += (index > 0 ? "/" : "") + part;

        const span = document.createElement("span");
        span.textContent = part;
        span.style.cursor = "pointer";
        span.addEventListener("click", () => goToFolder(accumulatedPath));
        filePathName.appendChild(span);

        if (index < parts.length - 1) {
            const sep = document.createElement("span");
            sep.textContent = " > ";
            filePathName.appendChild(sep);
        }
    });
}



function onItemDoubleClick(item) {
    if (item.type === "folder") {
        goToFolder(item.fullPath);
    } else {
        showImagePreview(item);
    }
}

function showImagePreview(item) {
   
    sideBar.style.visibility = "hidden";
    topMenu.style.visibility = "hidden";
    secondaryMenu.style.visibility = "hidden";
    headerColumn.style.visibility = "hidden";
    fileListing.style.visibility = "hidden";

    
    imgPreview.style.visibility = "visible";

   
    const previewImg = imgPreview.querySelector(".image-container img");
    previewImg.src = item.file_path || item.src || item.icon || '';

    
    const fileNameDiv = imgPreview.querySelector(".file-name");
    fileNameDiv.textContent = item.file_name || '';

    
    currentImgSrc = previewImg.src;
    currentFileName = fileNameDiv.textContent;
}


window.addEventListener("load", async () => {
    currentPath = localStorage.getItem("currentPath") || "";
    await fetchUserData();
    await refreshCurrentView(currentPath);
});

function goToRoot() {
  goToFolder("");
}



document.addEventListener("DOMContentLoaded", function() {
  let manageBtn = document.getElementById("manage-btn");
  let manageContextMenu = document.getElementById("manage-context-menu");
  let trashedSide = document.getElementById("trashed-side");
  let manageContent = document.querySelector(".manage-content");
  let closeIcon = document.querySelector(".close-icon");

  closeIcon.addEventListener("click", async function () {
  manageContent.style.display = "none";         
  toggleMainUI(true);

 
  await fetchUserData();                          
  await refreshCurrentView(currentPath);                         
});


  manageBtn.addEventListener("click", function() {
    manageContextMenu.style.visibility = "visible";
  });

  trashedSide.addEventListener("click", async function () {
    manageContent.style.display = "block";
    manageContextMenu.style.visibility = "hidden";
    toggleMainUI(false);

    const trashRows = document.querySelector(".trash-rows");
    trashRows.innerHTML = ""; 

    try {
     
      const response = await fetch(`${BACKEND_URL}/api/trash/user/${currentUserId}`);
      const data = await response.json();
      if (data.success) {
        data.trash.forEach(trashItem => appendToTrash(trashItem, trashItem.item_type));
      } else {
        trashRows.innerHTML = "<p style='padding: 20px; color: #666;'>No trash items found.</p>";
      }
    } catch (err) {
      trashRows.innerHTML = "<p style='padding: 20px; color: red;'>Error loading trash.</p>";
    }
  });
});




async function moveToTrash(item, type) {
  if (!item) return;

  const itemId = type === "folder" ? item.folder_id : item.file_id;

  try {
    const response = await fetch(`${BACKEND_URL}/api/trash`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        item_type: type,
        item_id: itemId,
        deleted_by: Number(currentUserId)
      })
    });

    const data = await response.json();

    if (data.success) {
     
      item.trash_id = data.trash_id;
      item.deleted_at = data.deleted_at;

      if (type === "folder") {
        currentFolders = currentFolders.filter(f => f.folder_id !== itemId);
      } else {
        currentFiles = currentFiles.filter(f => f.file_id !== itemId);
      }
      
        if (type === "folder") {
        parsedData.folders = parsedData.folders.filter(f => f.folder_id !== itemId);
        } else {
        parsedData.files = parsedData.files.filter(f => f.file_id !== itemId);
        }

      
      const itemDiv = document.querySelector(`.zwd-item[data-id="${itemId}"]`);
      if (itemDiv) itemDiv.remove();

     
      appendToTrash(item, type);
      await fetchUserData();               
      await refreshCurrentView(currentPath); 


    } else {
      alert("Failed to move to trash.");
    }
  } catch (err) {
    alert("Error moving to trash: " + err.message);
  }
}



async function restoreItem(item, row) {
  if (!item) return;
  try {
   
    const response = await fetch(`${BACKEND_URL}/api/trash/${item.trash_id}`, {
      method: 'DELETE'
    });
    const data = await response.json();
    if (data.success) {
      row.remove();        
      await goToFolder(currentPath);
    } else {
      alert("Failed to restore item.");
    }
  } catch (err) {
    alert("Error restoring item: " + err.message);
  }
}
async function deleteForever(item, row) {
  if (!item) return;
  try {
    const response = await fetch(`${BACKEND_URL}/api/trash/${item.trash_id}?permanent=true`, {
    method: 'DELETE'
    });

    const data = await response.json();
    if (data.success) {
      row.remove();

      if (item.file_name) {
        currentFiles = currentFiles.filter(f => f.trash_id !== item.trash_id && f.file_id !== item.file_id);
      } else if (item.folder_name) {
        currentFolders = currentFolders.filter(f => f.trash_id !== item.trash_id && f.folder_id !== item.folder_id);
      }

      const trashRows = document.querySelector(".trash-rows");
      if (trashRows.children.length === 0) {
        trashRows.innerHTML = "<p style='padding: 20px; color: #666;'>Trash is empty.</p>";
      }

     
    } else {
      alert("Failed to delete item permanently.");
    }
  } catch (err) {
    alert("Error deleting item: " + err.message);
  }
}


function appendToTrash(item, type) {
  const trashRows = document.querySelector(".trash-rows");

  const id = item.public_id || item.fullPath || item.file_name || item.trash_id;
  if (trashRows.querySelector(`[data-id="${id}"]`)) return;

  const row = document.createElement("div");
  row.className = "row";
  row.setAttribute("data-id", id);
  row.setAttribute("data-name", item.folder_name || item.file_name);

  row.innerHTML = `
    <div class="file-left" style="width:50%;">
        <div class="file-thumb">
            ${type === "folder" ? foldersvg : `
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <rect x="3" y="4" width="18" height="14" rx="2" stroke="#10b981" stroke-width="1.3" fill="#f0fdf4"/>
                    <path d="M7 12l2.5 3 3.5-4.5L17 16" stroke="#10b981" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>`
            }
        </div>
        <div class="file-meta">
            <div class="file-name">${item.folder_name || item.file_name}</div>
            <div class="file-sub">${item.deleted_at ? new Date(item.deleted_at).toLocaleString() : ''}</div>
        </div>
    </div>
    <div class="trashed-time" style="width: 50%; text-align: left;">
        ${item.deleted_at ? new Date(item.deleted_at).toLocaleString() : ''}
    </div>
    <div class="actions">
        <button class="restore-btn" title="Restore">Restore</button>
        <button class="delete-btn" title="Delete Permanently">Delete</button>
    </div>
`;

  row.querySelector(".restore-btn").addEventListener("click", () => restoreItem(item, row));
  row.querySelector(".delete-btn").addEventListener("click", () => deleteForever(item, row));

  trashRows.appendChild(row);
}


trashMenu.addEventListener('click', async function () {
 
    console.log("trashmenu clicked")
  const checkboxes = document.querySelectorAll('.custom-checkbox');
  console.log("All checkboxes found:", checkboxes.length);

  let itemsTrashed = 0;

  for (const chk of checkboxes) {
   if (chk.checked) {
  const itemDiv = chk.closest('.zwd-item');
  console.log("Found itemDiv:", itemDiv);

  const itemName = itemDiv.querySelector('.item-name')?.textContent.trim();
  const itemId = itemDiv.getAttribute('data-id');
  console.log("itemName:", itemName, "itemId:", itemId);

  
  console.log("currentFiles:", currentFiles);
  console.log("currentFolders:", currentFolders);

  let item = currentFiles.find(f => 
    String(f.public_id) === itemId || f.file_name === itemName
  );
  let type = 'file';

  if (!item) {
    item = currentFolders.find(f => 
      String(f.fullPath) === itemId || f.folder_name === itemName
    );
    if (item) {
      type = 'folder';
    }
  }

  console.log("Matched item:", item, "Type:", type);

  if (item) {
    await moveToTrash(item, type);
    chk.checked = false;
    itemsTrashed++;
  } else {
    console.warn("No matching item found in currentFiles or currentFolders.");
  }
}


  }

  if (itemsTrashed > 0) {
    headerColumn.style.visibility = "visible";
    document.querySelector(".selection-bar").style.visibility = "hidden";
    document.querySelector(".number-selected").textContent = "0";
  }
});

function onFolderNavigate() {
  selectedFiles = 0;
  selectedFolders = 0;

  selectiondiv.style.visibility = "hidden";
  headerColumn.style.visibility = "visible";

  document.querySelectorAll(".custom-checkbox").forEach(cb => cb.checked = false);

  itemsselectedno.textContent = "";
}


document.addEventListener("DOMContentLoaded", async function() {
  currentPath = "";
  await goToFolder(currentPath);
});






//share


async function shareItem(item, type) {
  const shared_with = prompt("Enter the username or user ID to share with:");
  if (!shared_with) return;

  const response = await fetch(`${BACKEND_URL}/api/share/${type}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      file_id: item.file_id || item.folder_id,
      shared_by: currentUserId,
      shared_with
    })
  });

  const data = await response.json();
  alert(data.message);
}
document.getElementById("sharingto").addEventListener("click", async () => {
  
  if (!currentItem || !currentType) {
    return alert("Please select a file or folder to share.");
  }

  const username = document.querySelector(".input-email").value.trim();
  if (!username) return alert("Please enter a username to share with.");

  
  const userRes = await fetch(`${BACKEND_URL}/api/users/username/${username}`);
  const userData = await userRes.json();

  if (!userData.success || !userData.user) {
    return alert("User not found. Please check the username.");
  }

  const shared_with = userData.user.user_id;
  console.log("Sharing payload:", {
  file_id: currentItem.file_id || currentItem.folder_id,
  shared_by: currentUserId,
  shared_with,
  type: currentType
});

  
  const response = await fetch(`${BACKEND_URL}/api/share/${currentType}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    // body: JSON.stringify({
    //   file_id: currentItem.file_id || currentItem.folder_id,
    //   shared_by: currentUserId,
    //   shared_with
    // })
    body: JSON.stringify({
    folder_id: currentType === "folder" ? currentItem.folder_id : undefined,
    file_id: currentType === "file" ? currentItem.file_id : undefined,
    shared_by: currentUserId,
    shared_with
  })

  });

  const data = await response.json();
  if (data.success) {
    alert("Shared successfully with " + username);
    document.querySelector(".input-email").value = "";
  } else {
    alert("Failed to share: " + data.message);
  }
});




const sharedWithMeBtn = document.getElementById("shared-with-me");

sharedWithMeBtn.addEventListener("click", async () => {
  console.log("Loading shared items...");
  await loadSharedWithMe();
});

async function loadSharedWithMe() {
  try {
    const res = await fetch(`${BACKEND_URL}/api/shared/${currentUserId}`);
    const data = await res.json();

    if (!data.success) {
      fileListing.innerHTML = "<p style='padding: 20px; color: red;'>Failed to load shared items.</p>";
      return;
    }

    fileListing.innerHTML = ""; 

    const sharedFolders = data.shared_folders || [];
    const sharedFiles = data.shared_files || [];

   
    function formatDate(dateValue) {
  if (!dateValue) return "";
  const d = new Date(dateValue);
  return isNaN(d.getTime()) ? "" : d.toLocaleDateString(); 
}

   
    const normalizedFolders = sharedFolders.map(f => ({
      folder_id: f.folder_id,
      folder_name: f.folder_name,
      uploadedBy: f.shared_by,
      uploadedDate: formatDate(f.created_at),
      type: "folder"
    }));

  
    const normalizedFiles = sharedFiles.map(f => ({
      file_id: f.file_id,
      file_name: f.file_name,
      file_path: f.file_path,
      uploadedBy: f.shared_by,
      uploadedDate: formatDate(f.created_at),
      type: "file"
    }));

    [...normalizedFolders, ...normalizedFiles].forEach(item => {
        console.log("Rendering item:", item);
        createItem(item);
        });


    if (normalizedFolders.length === 0 && normalizedFiles.length === 0) {
      fileListing.innerHTML = "<p style='padding: 20px; color: #666;'>No shared items found.</p>";
    }

  } catch (err) {
    console.error("Error loading shared items:", err);
    fileListing.innerHTML = "<p style='padding: 20px; color: red;'>Error loading shared items.</p>";
  }
}





const myFoldersBtn = document.getElementById("my-folders");

myFoldersBtn.addEventListener("click", async () => {

  console.log("Loading my folders...");
  await fetchUserData();             
  await refreshCurrentView("");      
});
