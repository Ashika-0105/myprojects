import React from 'react';
import './Modal.css';

export const Modal = ({ isOpen, type, title, message, onConfirm, onCancel, confirmText }) => {
  if (!isOpen) return null;

  const isDelete = type === 'delete';

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h3 className="modal-title">{title}</h3>
        <p className="modal-message">{message}</p>
        
        <div className="modal-actions">
          <button 
            className={`confirm-btn ${isDelete ? 'delete-theme' : 'primary-theme'}`} 
            onClick={onConfirm}
          >
            {confirmText || 'Confirm'}
          </button>
          
          <button className="cancel-btn" onClick={onCancel}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};