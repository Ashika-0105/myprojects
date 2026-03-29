import React, { useState, useEffect } from 'react'
import './Sidebar.css' 
import { Button } from "../Button/Button"; 
import { File } from "../File/File";

export const Sidebar = ({ theme, onToggleTheme, onFileClick, refreshTrigger }) => {
  const [files, setFiles] = useState([]);

  useEffect(() => {
    fetch('http://localhost:3005/getall-files')
      .then(res => res.json())
      .then(data => setFiles(data))
      .catch(err => console.error("Sidebar Error:", err));
  }, [refreshTrigger]);

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <p id='my-documents'>MY DOCUMENTS</p>
        <Button 
          className="new-document-btn" 
          onClick={() => onFileClick(null)} 
        >
          + New Document
        </Button>
      </div>

      <div className="file-list">
        {files.map((file) => (
          <File 
            key={file.id} 
            name={file.filename} 
            onClick={() => onFileClick(file.filename)} 
          />
        ))}
      </div>

      <div className='theme-toggle'>
          <img src='/assets/icon-light-mode.svg' alt="light" />
          <div id='toggle-button' onClick={onToggleTheme}>
            <div id="toggle" className={theme}></div>
          </div>
          <img src='/assets/icon-dark-mode.svg' alt="dark" />
      </div>
    </aside>
  );
};