export function Button({ children, variant = 'primary', className = '', ...props }) {
  const variants = {
    primary: 'bg-violet-600 text-white hover:bg-violet-700 shadow-sm shadow-violet-200',
    secondary: 'border border-zinc-200 bg-white text-zinc-800 hover:border-violet-300 hover:text-violet-700',
  }

  return (
    <button
      className={`inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-violet-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}
