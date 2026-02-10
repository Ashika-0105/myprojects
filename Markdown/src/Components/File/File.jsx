import React from 'react'
import './File.css' 

export const File = ({ name, date, onClick }) => {
  return (
    <div className='file-item' onClick={onClick} style={{ cursor: 'pointer' }}>
        <img src='/assets/icon-document.svg' alt="doc icon" />
        <div className="file-info">
          <p className="file-date">{date || "Recent"}</p>
          <p className="file-name">{name}</p>
        </div>
    </div>
  )
}