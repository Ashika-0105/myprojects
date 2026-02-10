import React from 'react'
import './Preview.css'

export const Preview = ({ text, isPreviewFull, isSidebarOpen, onPreviewToggle }) => {
  
  const renderMarkdown = (rawText) => {
    let html = rawText;

    html = html.replace(/^# (.*$)/gim, '<h1>$1</h1>');
    html = html.replace(/^## (.*$)/gim, '<h2>$1</h2>');
    html = html.replace(/^### (.*$)/gim, '<h3>$1</h3>');

    html = html.replace(/\*\*(.*?)\*\*/g, '<b>$1</b>');
    html = html.replace(/\*(.*?)\*/g, '<i>$1</i>');

    html = html.replace(/^\s*-\s+(.*$)/gim, '<ul><li>$1</li></ul>');
    html = html.replace(/<\/ul>\s*<ul>/g, '');

    html = html.replace(/\n/g, '<br/>');

    return html;
  };

  const getIcon = () => {
    if (isPreviewFull) return '/assets/icon-hide-preview.svg';
    return '/assets/icon-show-preview.svg';
  };

  return (
    <div className={`preview-container ${isPreviewFull ? 'full-preview' : ''}`}>
      <div className="preview-header">
         <p>PREVIEW</p>
        <div className="preview-toggle" onClick={onPreviewToggle}>
            <img src={getIcon()} alt="toggle preview" />
        </div>
      </div>
      
      <div 
        className="markdown-content"
        dangerouslySetInnerHTML={{ __html: renderMarkdown(text) }} 
      />
    </div>
  );
}