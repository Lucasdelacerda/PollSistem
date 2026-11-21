import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Menu, X, UserRound, LogOut, LogIn, UserPlus } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export function Header() {
  const [open, setOpen] = useState(false)
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    setOpen(false)
    navigate('/')
  }

  return (
    <header className="fixed inset-x-0 top-0 z-50 border-b border-violet-100 bg-white/90 backdrop-blur-xl">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 sm:px-6">
        <Link to="/" className="flex items-center gap-2.5 text-xl font-bold tracking-tight text-violet-700">
          <img src="/polls-logo.svg" alt="" className="size-9 shrink-0" />
          Enquetes
        </Link>

        <div className="relative">
          <button
            type="button"
            onClick={() => setOpen((value) => !value)}
            className="grid size-10 place-items-center rounded-xl border border-zinc-200 text-zinc-700 transition hover:border-violet-300 hover:text-violet-700"
            aria-label="Abrir menu"
            aria-expanded={open}
          >
            {open ? <X size={20} /> : <Menu size={20} />}
          </button>

          {open && (
            <nav className="absolute right-0 mt-3 w-56 overflow-hidden rounded-2xl border border-zinc-200 bg-white p-2 shadow-xl shadow-violet-100/50">
              {user ? (
                <>
                  <div className="px-3 py-2">
                    <p className="text-xs text-zinc-500">Conectado como</p>
                    <p className="truncate text-sm font-semibold text-zinc-900">{user.name}</p>
                  </div>
                  <Link to="/perfil" onClick={() => setOpen(false)} className="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-zinc-700 hover:bg-violet-50 hover:text-violet-700">
                    <UserRound size={17} /> Perfil
                  </Link>
                  <button type="button" onClick={handleLogout} className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-zinc-700 hover:bg-red-50 hover:text-red-600">
                    <LogOut size={17} /> Logout
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" onClick={() => setOpen(false)} className="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-zinc-700 hover:bg-violet-50 hover:text-violet-700">
                    <LogIn size={17} /> Login
                  </Link>
                  <Link to="/register" onClick={() => setOpen(false)} className="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-zinc-700 hover:bg-violet-50 hover:text-violet-700">
                    <UserPlus size={17} /> Register
                  </Link>
                </>
              )}
            </nav>
          )}
        </div>
      </div>
    </header>
  )
}
