const BACKEND_URL = "http://localhost:3000";
const taskName = document.getElementById("task-name");
const taskDescription = document.getElementById("task-description");
const taskList = document.querySelector(".tasklist");
const createTask = document.getElementById("create-task");

let editingTaskId = null; 


createTask.addEventListener("click", async () => {
  if (editingTaskId) {
    await updateTask(editingTaskId);
  } else {
    await addTask();
  }
});


async function loadTasks() {
  const response = await fetch(`${BACKEND_URL}/api/tasks`);
  const data = await response.json();

  taskList.innerHTML = "";

  for (let i = 0; i < data.length; i++) {
    createDiv(data[i].title, data[i].description, data[i].id);
  }
}


async function addTask() {
  const title = taskName.value.trim();
  const description = taskDescription.value.trim();

  if (!title || !description) {
    alert("Please fill both fields!");
    return;
  }

  await fetch(`${BACKEND_URL}/api/tasks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, description }),
  });

  taskName.value = "";
  taskDescription.value = "";
  await loadTasks();
}


async function updateTask(id) {
  const title = taskName.value.trim();
  const description = taskDescription.value.trim();

  if (!title || !description) {
    alert("Please fill both fields!");
    return;
  }

  await fetch(`${BACKEND_URL}/api/tasks/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, description }),
  });

  taskName.value = "";
  taskDescription.value = "";
  editingTaskId = null;
  createTask.textContent = "Create Task"; 
  await loadTasks();
}


function createDiv(title, description, id) {
  const newDiv = document.createElement("div");
  newDiv.classList.add("task");

  const taskname = document.createElement("div");
  taskname.textContent = title;
  taskname.classList.add("task-name");

  const desc = document.createElement("div");
  desc.textContent = description;
  desc.classList.add("description");

  const taskButtons = document.createElement("div");
  taskButtons.classList.add("task-buttons");

  const deleteButton = document.createElement("button");
  deleteButton.classList.add("delete");
  deleteButton.textContent = "Delete";
  deleteButton.addEventListener("click", async (e) => {
    await fetch(`${BACKEND_URL}/api/tasks/${id}`, { 
      method: "DELETE" 
    });
    await loadTasks();
  });

  const finishedButton = document.createElement("button");
  finishedButton.classList.add("finished");
  finishedButton.textContent = "Finished";

  taskButtons.append(deleteButton, finishedButton);
  newDiv.append(taskname, desc, taskButtons);

  
  newDiv.addEventListener("click", (e) => {
    if (e.target.tagName === "BUTTON") return;
    taskName.value = title;
    taskDescription.value = description;
    editingTaskId = id;
    createTask.textContent = "Update Task"; 
  });

  taskList.append(newDiv);
}


loadTasks();
