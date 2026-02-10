let parsedData = { files: [], folders: [], trash: [] };

async function fetchDataFromDB() {
  try {
    const response = await fetch('/api/getData', { method: 'GET', credentials: 'include' });
    if (!response.ok) throw new Error('Failed to fetch data from DB');
    const data = await response.json();
    return data || { files: [], folders: [], trash: [] };
  } catch (error) {
    console.error(error);
    return { files: [], folders: [], trash: [] };
  }
}

// Call it once to initialize parsedData
fetchDataFromDB().then(data => {
  parsedData = data;
  // You might want to call a function here to render your UI using parsedData,
  // e.g., renderItems(parsedData.files, parsedData.folders);
});
async function saveDataToDB(data) {
  try {
    const response = await fetch('/api/saveData', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Failed to save data');
  } catch (error) {
    console.error(error);
  }
}

// Example usage after updating parsedData:
saveDataToDB(parsedData);
let currentPath = ""; 
let selectedFolderPath = "";
let currentImgSrc = "";
let currentFileName = "";
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
// let headerColumn = document.querySelector(".header-column");
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
newButton.addEventListener("click", function() {
  sidebarNew.style.display = "block";
});


document.addEventListener("click", function(event) {
  if (!sidebarNew.contains(event.target) && !document.querySelector(".primary-button").contains(event.target)) {
    sidebarNew.style.display = "none";
  }
});


sidebarNew.addEventListener("click", function(event) {
  if (event.target.classList.contains("folder-btn")) {
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

function createItem(item) {
  const itemDiv = document.createElement("div");
  itemDiv.classList.add("zwd-item");
  if (item.public_id) {
    itemDiv.setAttribute('data-id', item.public_id);
  } else if (item.fullPath) {
    
    itemDiv.setAttribute('data-id', item.fullPath);
  }
  

  const contentDiv = document.createElement('div');
  contentDiv.classList.add('zwd-content');

  const checkBox = document.createElement('input');
  checkBox.type = 'checkbox';
  checkBox.classList.add('custom-checkbox');
  contentDiv.appendChild(checkBox);

  const imgContent = document.createElement('div');
  imgContent.classList.add('img-content');
   if(item.type.startsWith("image")){
    const imgElement = document.createElement("img");
    imgElement.src =  item.src;
    imgElement.classList.add('thumbnail');
    imgContent.appendChild(imgElement);
   }
   else if(item.type==="folder"){
    imgContent.innerHTML= foldersvg;
   }
  const infoContent = document.createElement('div');
  infoContent.classList.add('info-content');
  const itemName = document.createElement('span');
  itemName.classList.add('item-name');
  itemName.textContent = item.folder_name || item.file_name ;
  infoContent.appendChild(itemName);

  const uploadedInfoDiv = document.createElement('div');
  uploadedInfoDiv.classList.add('uploaded-info');
  uploadedInfoDiv.innerHTML = `<span>Uploaded by </span><span class="title">${item.uploadedBy}</span>`;
  infoContent.appendChild(uploadedInfoDiv);

  const modifiedDateDiv = document.createElement('div');
  modifiedDateDiv.classList.add('Modified-date');
  const dateSpan = document.createElement('span');
  dateSpan.classList.add('date');
  dateSpan.textContent = item.uploadedDate;
  modifiedDateDiv.appendChild(dateSpan);

  contentDiv.appendChild(imgContent);
  contentDiv.appendChild(infoContent);
  itemDiv.appendChild(contentDiv);
  itemDiv.appendChild(modifiedDateDiv);

  fileListing.appendChild(itemDiv);
   



// let selectiondiv = document.querySelector(".selection-bar");
// let itemsselectedno = document.querySelector(".number-selected");

itemDiv.addEventListener("click", (e) => {
  if (e.target === checkBox) return;

  checkBox.checked = !checkBox.checked;
  
  let selectedFiles = 0;
let selectedFolders = 0;


document.querySelectorAll(".zwd-item").forEach(item => {
  const checkbox = item.querySelector(".custom-checkbox");
  const isChecked = checkbox && checkbox.checked;
  const isFolder = item.querySelector(".img-content svg"); 

  if (isChecked) {
    if (isFolder) {
      selectedFolders += 1;
    } else {
      selectedFiles += 1;
    }
  }
});


let parts = [];
if (selectedFolders > 0) parts.push(`${selectedFolders} folder${selectedFolders > 1 ? "s" : ""}`);
if (selectedFiles > 0) parts.push(`${selectedFiles} file${selectedFiles > 1 ? "s" : ""}`);

const displayText = parts.length > 0 ? `${parts.join(", ")} selected` : "";

itemsselectedno.textContent = displayText;

// Toggle visibility
if (selectedFiles + selectedFolders > 0) {
  headerColumn.style.visibility = "hidden";
  selectiondiv.style.visibility = "visible";
} else {
  headerColumn.style.visibility = "visible";
  selectiondiv.style.visibility = "hidden";
}

});

//this is to show the img

itemDiv.addEventListener("dblclick", function () {
   
  const isFolder = item.type === "folder";
  if (isFolder) {
    goToFolder(item.fullPath || item.folder_name); 
    filePath.style.display = "flex";
    return;
} 

else {
  sideBar.style.visibility = "hidden";
  topMenu.style.visibility = "hidden";
  secondaryMenu.style.visibility = "hidden";
  headerColumn.style.visibility = "hidden";
  fileListing.style.visibility = "hidden";
  imgPreview.style.visibility = "visible";

  const previewImg = imgPreview.querySelector(".image-container img");
  const imgSrc = item.src || item.icon;
  previewImg.src = imgSrc;  

  console.log("Image source to display:", imgSrc);

  const fileNameDiv = imgPreview.querySelector(".file-name");
  fileNameDiv.textContent = item.file_name;

  currentImgSrc = imgSrc;    
  currentFileName = item.file_name;
}
function makeDownloadableCloudinaryUrl(originalUrl, fileName) {
 
  const cleanFileName = (fileName || 'download').replace(/\.[^/.]+$/, "");
  const parts = originalUrl.split("/upload/");
  if (parts.length !== 2) return originalUrl;
  const transformation = `fl_attachment:${cleanFileName}`;
  return `${parts[0]}/upload/${transformation}/${parts[1]}`;
}

downloadBtn.addEventListener("click", function () {
  if (!currentImgSrc) {
    alert("No image selected to download.");
    return;
  }

  const downloadUrl = makeDownloadableCloudinaryUrl(currentImgSrc, currentFileName);
  console.log("Copying URL:", downloadUrl);
  const tempLink = document.createElement("a");
  tempLink.href = downloadUrl;
  tempLink.setAttribute("download", currentFileName || "downloaded-file.jpg");
  document.body.appendChild(tempLink);
  tempLink.click();
  document.body.removeChild(tempLink);
});
const copyBtn = document.getElementById("copy-link-btn");
copyBtn.addEventListener("click", function () {
  if (!currentImgSrc) return alert("No image selected.");

  
  navigator.clipboard.writeText(currentImgSrc)
    .then(() => alert("View link copied!"))
    .catch(() => alert("Could not copy link."));
});

});



let topButtons = document.querySelector(".top-buttons");
topButtons.addEventListener("click", function(event) {
  const escBtn = event.target.closest("#esc-button");
  if (escBtn) {
    console.log("Esc clicked");
    sideBar.style.visibility = "visible";
    topMenu.style.visibility = "visible";
    secondaryMenu.style.visibility = "visible";
    headerColumn.style.visibility = "visible";
    fileListing.style.visibility = "visible";
    imgPreview.style.visibility = "hidden";
  }
});
}

createFolderBtn.addEventListener("click", async function () {
  const folderName = folderNameInput.value.trim();

  if (folderName) {
    const uploader = localStorage.getItem("username") || "Anonymous";
    const id = Date.now() + Math.random().toString(36).substring(2, 5);
    const targetPath = selectedFolderPath || currentPath;

    const newFolder = {
      folder_id: id,
      folder_name: folderName,
      fullPath: targetPath ? `${targetPath}/${folderName}` : folderName,
      type: "folder",
      uploadedBy: uploader,
      uploadedDate: new Date().toLocaleDateString(),
    };

    parsedData.folders.push(newFolder);
    await saveDataToDB(parsedData); // ✅ Save to backend

    refreshCurrentView();
    closeModal();
    selectedFolderPath = "";
  } else {
    alert("Please enter a folder name!");
  }
});


async function refreshCurrentView() {
  // Fetch latest data from backend
  parsedData = await fetchDataFromDB();

  console.log("Refreshing view for path:", currentPath);
  if (currentPath !== "") {
    filePath.style.display = "flex";
  } else {
    filePath.style.display = "none";
  }

  fileListing.innerHTML = "";

  const subFolders = parsedData.folders.filter(folder => {
    if (!folder.fullPath) return false;
    const folderFullPath = folder.fullPath;
    const parentPath = folderFullPath.split("/").slice(0, -1).join("/");
    return parentPath === currentPath;
  });

  const filesInFolder = parsedData.files.filter(file => {
    if (!file.webkitRelativePath)  return currentPath === ""; 
    const fileFolderPath = file.webkitRelativePath.split("/").slice(0, -1).join("/");
    return fileFolderPath === currentPath;
  });

  subFolders.forEach(folder => createItem(folder));
  filesInFolder.forEach(file => createItem(file));
  renderBreadcrumb(currentPath);
}


///if thsi not works use old code
function renderBreadcrumb(folderPath) {
  filePathName.innerHTML = "";  
  filePathName.setAttribute("role", "navigation");
  filePathName.setAttribute("aria-label", "Breadcrumb navigation");

  const parts = folderPath ? folderPath.split("/") : [];

  if (parts.length === 0) {
    // Render root breadcrumb
    const rootSpan = document.createElement("span");
    rootSpan.classList.add("breadcrumb-part");
    rootSpan.textContent = "Root";
    rootSpan.style.cursor = "pointer";
    rootSpan.title = "";
    rootSpan.addEventListener("click", () => {
      goToFolder("");
    });
    filePathName.appendChild(rootSpan);
    return;
  }

  let fullPath = "";

  parts.forEach((part, index) => {
    fullPath += (index === 0 ? "" : "/") + part;

    const span = document.createElement("span");
    span.classList.add("breadcrumb-part");
    span.textContent = part;
    span.style.cursor = "pointer";
    span.title = fullPath;

    span.addEventListener("click", () => {
      console.log("Breadcrumb clicked:", fullPath);
      goToFolder(fullPath); 
    });

    filePathName.appendChild(span);

    if (index < parts.length - 1) {
      const separator = document.createElement("span");
      separator.classList.add("breadcrumb-separator");
      separator.innerHTML = `<svg aria-hidden="true" focusable="false" viewBox="0 0 24 24" class="arrow" width="12" height="12">
          <path d="M7.293 4.15a1 1 0 0 1 1.414 0l7.071 7.072a1 1 0 0 1 0 1.414l-7.07 7.071a1 1 0 0 1-1.415-1.414l6.363-6.364-6.363-6.364a1 1 0 0 1-.083-1.32l.083-.094Z"></path>
        </svg>`;
      filePathName.appendChild(separator);
    }
  });
}


//function goToRoot() {
//   currentPath = "";
//   localStorage.setItem("currentPath", currentPath);  // Add this line for consistency
//   refreshCurrentView();
// }

function goToRoot() {
  currentPath = "";
  refreshCurrentView();
}

function goToFolder(path) {
    currentPath = path;
    localStorage.setItem("currentPath", currentPath);
    console.log("Navigated to:", currentPath);
    onFolderNavigate();
    refreshCurrentView();
}

let moreActions = document.getElementById("more-actions");
let contextMenu = document.getElementById("context-menu");

moreActions.addEventListener("click", function () {
  contextMenu.style.visibility = "visible"; 
});

document.addEventListener("click", function (event) {
  if (!contextMenu.contains(event.target) && !moreActions.contains(event.target)) {
    contextMenu.style.visibility = "hidden";
  }
});

let displayValue1 = "";

console.log(document.getElementById('trash-menu'));
const trashMenu = document.getElementById('trash-menu');
function toggleMainUI(show = true) {
    const displayValue = show ? "visible" : "hidden";
    console.log("SHow : " + show);
    console.log("displayValue : " + displayValue)
    sideBar.style.visibility = displayValue;
    topMenu.style.visibility = displayValue;
    secondaryMenu.style.visibility = displayValue;
    headerColumn.style.visibility = displayValue;
    // selectiondiv.style.visibility= displayValue;
    fileListing.style.visibility = displayValue;
    displayValue1 = displayValue;
    
}





document.addEventListener("DOMContentLoaded", function() {
  let manageBtn = document.getElementById("manage-btn");
  let manageContextMenu = document.getElementById("manage-context-menu");
  let trashedSide = document.getElementById("trashed-side");
  let manageContent = document.querySelector(".manage-content");
  let closeIcon = document.querySelector(".close-icon");
  

closeIcon.addEventListener("click", function () {
  const manageContent = document.querySelector(".manage-content");

  manageContent.style.display = "none";         
  toggleMainUI(true);                          
  refreshCurrentView();                         
});


  manageBtn.addEventListener("click", function() {
    manageContextMenu.style.visibility = "visible";
  });


trashedSide.addEventListener("click", function () {
    manageContent.style.display = "block";
    manageContextMenu.style.visibility = "hidden";
    toggleMainUI(false);

    const trashRows = document.querySelector(".trash-rows");
    trashRows.innerHTML = ""; 

    parsedData.trash.forEach(trashItem => appendToTrash(trashItem, trashItem.type));
});


});


window.addEventListener("load", async function() {
    try {
        const response = await fetch('/api/items');
        if (!response.ok) throw new Error('Failed to fetch data from server');
        const data = await response.json();

        parsedData.files = data.files || [];
        parsedData.folders = data.folders || [];
        parsedData.trash = data.trash || [];

        refreshCurrentView();
        (parsedData.trash || []).forEach(trashItem => appendToTrash(trashItem, trashItem.type));
    } catch (error) {
        console.error(error);
        alert("Error loading data from server");
    }
});

parsedData.files = parsedData.files || [];
parsedData.folders = parsedData.folders || [];
parsedData.trash = parsedData.trash || [];

async function moveToTrash(item, type) {
    if (!item) return;

    try {
        const itemId = item.public_id || item.file_name || item.fullPath;
        const response = await fetch('/api/trash/move', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itemId, type }),
        });
        if (!response.ok) throw new Error('Failed to move to trash');

        // Remove from original list
        if (type === "folder") {
            parsedData.folders = parsedData.folders.filter(f => f.fullPath !== item.fullPath);
        } else {
            parsedData.files = parsedData.files.filter(f =>
                f.public_id ? f.public_id !== item.public_id : f.file_name !== item.file_name
            );
        }

        // Create trashed item with trashedTime
        const trashedItem = { ...item, type, trashedTime: new Date().toLocaleString() };

        // Add to trash
        parsedData.trash.push(trashedItem);

        // Persist changes
        localStorage.setItem("data", JSON.stringify(parsedData));

        // Update UI
        appendToTrash(trashedItem, type);
        refreshCurrentView();

    } catch (error) {
        console.error(error);
        alert("Error moving item to trash");
    }
}


async function restoreItem(item, row) {
    if (!item) return;

    try {
        const itemId = item.public_id || item.file_name || item.fullPath;
        const response = await fetch('/api/trash/restore', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itemId, type: item.type }),
        });
        if (!response.ok) throw new Error('Failed to restore item');

        parsedData.trash = parsedData.trash.filter(t =>
            t.public_id ? t.public_id !== item.public_id : t.file_name !== item.file_name
        );

        if (item.type === "folder") {
            parsedData.folders.push(item);
        } else {
            parsedData.files.push(item);
        }

        row.remove();
        refreshCurrentView();

    } catch (error) {
        console.error(error);
        alert("Error restoring item");
    }
}

async function deleteForever(item, row) {
    if (!item) return;

    try {
        const itemId = item.public_id || item.file_name || item.fullPath;
        const response = await fetch('/api/trash/delete', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itemId, type: item.type }),
        });
        if (!response.ok) throw new Error('Failed to delete item forever');

        parsedData.trash = parsedData.trash.filter(t =>
            t.public_id ? t.public_id !== item.public_id : t.file_name !== item.file_name
        );

        row.remove();

        const trashRows = document.querySelector(".trash-rows");
        if (trashRows.children.length === 0) {
            trashRows.innerHTML = "<p style='padding: 20px; color: #666;'>Trash is empty.</p>";
        }

    } catch (error) {
        console.error(error);
        alert("Error deleting item forever");
    }
}



function appendToTrash(item, type) {
    const trashRows = document.querySelector(".trash-rows");

    const id = item.public_id || item.fullPath || item.file_name;
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
                <div class="file-sub">${item.trashedTime}</div>
            </div>
        </div>
        <div class="trashed-time" style="width: 50%; text-align: left;">
            ${item.uploadedDate} by ${item.uploadedBy}
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
    const checkboxes = document.querySelectorAll('.custom-checkbox');
    let itemsTrashed = 0;

    for (const chk of checkboxes) {
        if (chk.checked) {
            const itemDiv = chk.closest('.zwd-item');
            const itemName = itemDiv.querySelector('.item-name').textContent;
            const itemId = itemDiv.getAttribute('data-id');

            let item = parsedData.files.find(f =>
                f.public_id === itemId || f.file_name === itemName
            );
            let type = 'file';

            if (!item) {
                item = parsedData.folders.find(f => f.fullPath === itemId);
                type = 'folder';
            }

            if (item) {
                try {
                    await moveToTrash(item, type);
                    chk.checked = false;
                    itemsTrashed++;
                } catch (error) {
                    console.error("Failed to move item to trash:", error);
                    alert(`Failed to trash "${itemName}". Please try again.`);
                }
            }
        }
    }

    if (itemsTrashed > 0) {
        headerColumn.style.visibility = "visible";
        document.querySelector(".selection-bar").style.visibility = "hidden";
        document.querySelector(".number-selected").textContent = "0";
    }
});
