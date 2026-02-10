import React, { useState, useRef } from 'react';
import './Editor.css';

export const Editor = ({ text, setText }) => {
  const [toolbarPos, setToolbarPos] = useState({ top: 0, left: 0, show: false });
  const textareaRef = useRef(null);

  const handleSelection = () => {
    const el = textareaRef.current;
    
    if (el.selectionStart !== el.selectionEnd) {
      const { top, left } = el.getBoundingClientRect();
      
      setToolbarPos({
        top: -40, 
        left: 20, 
        show: true
      });
    } else {
      setToolbarPos((prev) => ({ ...prev, show: false }));
    }
  };

  const applyStyle = (prefix, suffix = prefix) => {
    const el = textareaRef.current;
    const start = el.selectionStart;
    const end = el.selectionEnd;
    const selectedText = text.substring(start, end);

    const newText = 
      text.substring(0, start) + 
      prefix + selectedText + suffix + 
      text.substring(end);

    setText(newText);
    
    // Focus back on textarea after applying style
    setTimeout(() => {
      el.focus();
      setToolbarPos((prev) => ({ ...prev, show: false }));
    }, 10);
  };

  return (
    <div className="editor-container" style={{ position: 'relative' }}>
      {toolbarPos.show && (
        <div 
          className="toolbar-popup" 
          style={{ 
            position: 'absolute', 
            top: toolbarPos.top, 
            left: toolbarPos.left,
            zIndex: 10
          }}
        >
          <button onMouseDown={(e) => { e.preventDefault(); applyStyle('**'); }}>B</button>
          <button onMouseDown={(e) => { e.preventDefault(); applyStyle('_'); }}>I</button>
          <button onMouseDown={(e) => { e.preventDefault(); applyStyle('# ', ''); }}>H</button>
        </div>
      )}

      <div className="editor-header">
        <p>MARKDOWN</p>
      </div>
      
      <textarea 
        ref={textareaRef}
        className="editor-textarea" 
        value={text} 
        onChange={(e) => setText(e.target.value)} 
        onSelect={handleSelection}
      />
    </div>
  );
};