import React from 'react'

export const Button = ({ children, className, onClick }) => {
  return (
   <button className={className} onClick={onClick}>
     {children} 
   </button>
  )
}