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
const selectiondiv = document.querySelector(".selection-bar");
const itemsselectedno = document.querySelector(".number-selected");
const headerColumn = document.querySelector(".header-column");

let downloadBtn = document.getElementById("download-btn");

let parsedData = { files: [], folders: [], trash: [] };  // Declare globally

fetch('http://localhost:3000/api/data')  // API endpoint to get data
  .then(res => res.json())
  .then(data => {
    parsedData = data || { files: [], folders: [], trash: [] };  // Update the global parsedData
    console.log("Fetched data:", parsedData);
  })
  .catch(err => console.error("Error fetching data:", err));

function updateDataToBackend(updatedData) {
  fetch('http://localhost:3000/api/update', {  // API endpoint to update the data
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updatedData)  // Send the updated data to the backend
  })
  .then(res => res.json())
  .then(updatedData => {
    console.log("Data updated:", updatedData);
  })
  .catch(err => console.error("Error updating data:", err));
}
//all same 

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


// Folder creation logic
createFolderBtn.addEventListener("click", function() {
  const folderName = folderNameInput.value.trim();

  if (folderName) {
    let targetPath = selectedFolderPath || currentPath;

    let newFolder = {
      folder_name: folderName,
      fullPath: targetPath ? `${targetPath}/${folderName}` : folderName,
      type: "folder",
      uploadedBy: localStorage.getItem("username"), // Dynamic username
      uploadedDate: new Date().toLocaleDateString()
    };

    // Send POST request to the backend to create the new folder
    fetch('http://localhost:3000/api/folders', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newFolder)
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        parsedData.folders = data.folders;  // Assuming the response contains updated folder data
        updateDataToBackend(parsedData); // Sync backend
        refreshCurrentView();
        closeModal();
        selectedFolderPath = ""; // Reset folder path
      } else {
        alert("Error creating folder: " + (data.message || "Something went wrong"));
      }
    })
    .catch(err => {
      console.error("Error creating folder:", err);
      alert("Error creating folder.");
    });
  } else {
    alert("Please enter a folder name!");
  }
});

// Upload file handler with file validation
sidebarNew.addEventListener("click", function(event) {
  if (event.target.classList.contains("upload-file-btn")) {
    let inputfilebtn = document.getElementById("filepicker");
    inputfilebtn.click();
    inputfilebtn.onchange = async function () {
      for (let i = 0; i < inputfilebtn.files.length; i++) {
        let file = inputfilebtn.files[i];

        // Validate file type and size (for example, limit file size to 10MB)
        if (file.size > 10 * 1024 * 1024) {
          alert("File size exceeds 10MB limit.");
          continue;
        }

        if (!file.type.startsWith('image/')) {
          alert("Only image files are allowed.");
          continue;
        }

        try {
          const cloudinaryUrl = await uploadToCloudinary(file);
          let id = parsedData.files.length;
          let targetPath = selectedFolderPath || currentPath;
          let newFile = {
            file_id: id,
            file_name: file.name,
            type: file.type,
            size: file.size,
            uploadedBy: localStorage.getItem("username"), // Dynamic username
            uploadedDate: new Date().toLocaleDateString(),
            src: cloudinaryUrl,
            webkitRelativePath: (targetPath ? targetPath : "") + "/" + file.name
          };

          parsedData.files.push(newFile);
          updateDataToBackend(parsedData); // Sync backend
          localStorage.setItem("data", JSON.stringify(parsedData));
          refreshCurrentView(selectedFolderPath || currentPath);
          selectedFolderPath = ""; // Reset folder path
        } catch (err) {
          alert("Upload to Cloudinary failed: " + err.message);
        }
      }
    };
  }
});

// Folder upload logic
sidebarNew.addEventListener("click", function(event) {
  if (event.target.classList.contains("upload-folder-btn")) {
    let inputfolderbtn = document.getElementById("folderpicker");
    inputfolderbtn.click();
    inputfolderbtn.onchange = async function () {
      const folderSet = new Set();

      for (let file of inputfolderbtn.files) {
        let basePath = selectedFolderPath || currentPath;
        const parts = file.webkitRelativePath.split("/");
        parts.pop();
        let path = basePath;
        for (let part of parts) {
          path = path ? path + "/" + part : part;

          if (!folderSet.has(path)) {
            folderSet.add(path);
            if (!parsedData.folders.some(f => f.fullPath === path)) {
              parsedData.folders.push({
                folder_id: parsedData.folders.length,
                folder_name: path.split("/").pop(),
                fullPath: path,
                type: "folder",
                uploadedBy: localStorage.getItem("username"), // Dynamic username
                uploadedDate: new Date().toLocaleDateString()
              });
            }
          }
        }
      }

      try {
        for (let file of inputfolderbtn.files) {
          const cloudinaryUrl = await uploadToCloudinary(file);
          let basePath = selectedFolderPath || currentPath;
          let newFile = {
            file_id: parsedData.files.length,
            file_name: file.name,
            type: file.type,
            size: file.size,
            uploadedBy: localStorage.getItem("username"), // Dynamic username
            uploadedDate: new Date().toLocaleDateString(),
            src: cloudinaryUrl,
            webkitRelativePath: [basePath, file.webkitRelativePath].filter(Boolean).join("/")
          };

          parsedData.files.push(newFile);
        }
      } catch (err) {
        alert("Upload failed: " + err.message);
      }

      updateDataToBackend(parsedData); // Sync backend
      localStorage.setItem("data", JSON.stringify(parsedData));
      refreshCurrentView();
      selectedFolderPath = ""; // Reset folder path
    };
  }
});

async function uploadToCloudinary(file) {
  const url = "https://api.cloudinary.com/v1_1/dheizv6u0/image/upload";
  const formData = new FormData();
  formData.append('file', file);
  formData.append('upload_preset', 'Workdrive');

  const response = await fetch(url, { method: 'POST', body: formData });

  if (!response.ok) {
    throw new Error('Cloudinary upload failed');
  }

  const data = await response.json();
  return data.secure_url;
}


function closeModal() {
  modal.classList.remove('active');
  overlay.classList.remove('active');
  folderNameInput.value = ''; // Clear folder name input
}


function refreshCurrentView() {
  console.log("Refreshing view for path:", currentPath); 
  if (currentPath !== "") {
    filePath.style.display = "flex";
  } else {
    filePath.style.display = "none";
  }
  console.log("Current Path:", currentPath);
  fileListing.innerHTML = "";

  const subFolders = parsedData.folders.filter(folder => {
    if (!folder.fullPath) return false;
    const folderFullPath = folder.fullPath;
    const parentPath = folderFullPath.split("/").slice(0, -1).join("/");
    return parentPath === currentPath;
  });
  console.log("Subfolders:", subFolders.map(f => f.fullPath));
  const filesInFolder = parsedData.files.filter(file => {
    if (!file.webkitRelativePath)  return currentPath === ""; 
    const fileFolderPath = file.webkitRelativePath.split("/").slice(0, -1).join("/");
    return fileFolderPath === currentPath;
  });
  console.log("Files:", filesInFolder.map(f => f.webkitRelativePath));
  subFolders.forEach(folder => createItem(folder));
  filesInFolder.forEach(file => createItem(file));
  renderBreadcrumb(currentPath);
}

function renderBreadcrumb(folderPath) {
  filePathName.innerHTML = "";  

  const parts = folderPath ? folderPath.split("/") : [];

  if (parts.length === 0) return;  

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
      // currentPath = fullPath;
      // refreshCurrentView();
       goToFolder(fullPath); 
    });

    filePathName.appendChild(span);

    if (index < parts.length - 1) {
      const separator = document.createElement("span");
      separator.classList.add("breadcrumb-separator");
      separator.innerHTML = `<svg aria-label="items" DataSvgName="wd_zdrhtarrow" viewBox="0 0 24 24" class="arrow">
          <path d="M7.293 4.15a1 1 0 0 1 1.414 0l7.071 7.072a1 1 0 0 1 0 1.414l-7.07 7.071a1 1 0 0 1-1.415-1.414l6.363-6.364-6.363-6.364a1 1 0 0 1-.083-1.32l.083-.094Z"></path>
        </svg>`;
      filePathName.appendChild(separator);
    }
  });
}
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
window.addEventListener('load', async () => {
  try {
    const response = await fetch('/api/items');
    const data = await response.json();

    // populate your parsedData or directly update UI from data
    parsedData.files = data.files || [];
    parsedData.folders = data.folders || [];
    parsedData.trash = data.trash || [];

    refreshCurrentView();
    (parsedData.trash || []).forEach(trashItem => appendToTrash(trashItem, trashItem.type));
  } catch (error) {
    console.error('Failed to load data from server', error);
  }
});
