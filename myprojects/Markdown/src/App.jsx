import { useState } from 'react'
import './App.css'
import { Sidebar } from './Components/Sidebar/Sidebar'
import { Header } from './Components/Header/Header'
import { Editor } from './Components/Editor/Editor'
import { Preview } from './Components/Preview/Preview'

function App() {
  const [hasSelectedFile, setHasSelectedFile] = useState(false);
  const [fileName, setFileName] = useState("");
  const [theme, setTheme] = useState('dark'); 
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isPreviewFull, setIsPreviewFull] = useState(false); 
  const [markdownText, setMarkdownText] = useState("");
  const [refreshSidebar, setRefreshSidebar] = useState(0);
  const [originalFileName, setOriginalFileName] = useState("");
  const handleExit = () => {
    setHasSelectedFile(false);
    setFileName("");
    setMarkdownText("");
  };


  const [isExistingFile, setIsExistingFile] = useState(false);

const handleFileSelection = (name) => {
  if (!name) {
    setHasSelectedFile(true);
    setFileName("untitled.md");
    setOriginalFileName("untitled.md");
    setMarkdownText("# New Document\nStart typing here...");
    setIsExistingFile(false);
    return;
  }
  fetch(`http://localhost:3005/get-file?name=${name}`)
    .then(res => res.json())
    .then(data => {
      setHasSelectedFile(true);
      setFileName(data.filename);
      setOriginalFileName(data.filename); 
      setMarkdownText(data.content);
      setIsExistingFile(true);
    });
};

const handleSave = () => {
  const endpoint = isExistingFile ? '/update-file' : '/save-file';
  const method = isExistingFile ? 'PUT' : 'POST';

  fetch(`http://localhost:3005${endpoint}`, {
    method: method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ 
      originalName: originalFileName, 
      filename: fileName,             
      content: markdownText 
    })
  })
  .then(res => {
    if (!res.ok) throw new Error("Save failed");
    return res.json();
  })
  .then(data => {
    console.log(data.message);
    setOriginalFileName(fileName); 
    setIsExistingFile(true);
    setRefreshSidebar(prev => prev + 1);
  })
  .catch(err => console.log("Save failed. Use Ctrl+F5 to clear browser cache."));
};
  const handleDelete = () => {
    // if (window.confirm(`Delete ${fileName}?`)) {
      fetch(`http://localhost:3005/delete-file?name=${fileName}`, { method: 'DELETE' })
        .then(() => {
          handleExit();
          setRefreshSidebar(prev => prev + 1);
        });
    // }
  };

  return (
    <div className={`app-container ${theme} ${isSidebarOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
      {isSidebarOpen && (
        <Sidebar 
          theme={theme} 
          onToggleTheme={() => setTheme(t => t === 'dark' ? 'light' : 'dark')} 
          onFileClick={handleFileSelection}
          refreshTrigger={refreshSidebar}
        />
      )}

      <main className="main-content">
        {hasSelectedFile ? (
          <>
            <Header 
              filename={fileName} 
              setFileName={setFileName} 
              isSidebarOpen={isSidebarOpen} 
              onToggle={() => setIsSidebarOpen(!isSidebarOpen)} 
              onSave={handleSave}
              onDelete={handleDelete}
              onExit={handleExit} 
              isNewFile={fileName === "untitled.md"} 
            />
            <div className="workspace">
              {!isPreviewFull && <Editor text={markdownText} setText={setMarkdownText} />}
              <Preview 
                text={markdownText} 
                isPreviewFull={isPreviewFull} 
                onPreviewToggle={() => setIsPreviewFull(!isPreviewFull)} 
              />
            </div>
          </>
        ) : (
          <div className="welcome-screen">
            <h2>Create new files and enjoy editing myapp</h2>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;