// src/Components/Header/Header.jsx
import React from 'react'
import './Header.css'

export const Header = ({ 
  filename, 
  setFileName, 
  isSidebarOpen, 
  onToggle, 
  onSave, 
  onDelete, 
  onExit, 
  isNewFile 
}) => {
  return (
    <header className="main-header">
      <div className="header-left">
        <div id="close" onClick={onToggle}>
          <img 
            src={isSidebarOpen ? '/assets/icon-close.svg' : '/assets/icon-menu.svg'} 
            alt="toggle sidebar" 
          />
        </div>
        <div className="logo">
           <img src='/assets/logo.svg' alt="logo" />
        </div>
        <div className="separator"></div>
        <div className="document-info">
          <img src='/assets/icon-document.svg' alt="doc" />
          <div className="document-text">
            <p className="doc-label">Document Name</p>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <input 
                className="doc-name-input"
                value={filename}
                onChange={(e) => setFileName(e.target.value)}
              />
              <img 
                src="/assets/icon-close.svg" 
                alt="exit" 
                style={{ cursor: 'pointer', width: '12px', height: '12px', opacity: 0.5 }} 
                onClick={onExit} 
              />
            </div>
          </div>
        </div>
      </div>
      
      <div className="header-right">
         <button className="delete-btn" onClick={onDelete}>
            <img src='/assets/icon-delete.svg' alt="delete" />
         </button>
         <button className="save-btn" onClick={onSave}>
            <img src='/assets/icon-save.svg' alt="save" />
            <span>{isNewFile ? 'Save' : 'Save Changes'}</span>
         </button>
      </div>
    </header>
  )
}