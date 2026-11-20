import { Navigate, Link } from 'react-router-dom'
import { LogOut, Plus, UserRound } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { Button } from '../components/Button'

export function ProfilePage() {
  const { user, logout } = useAuth()
  if (!user) return <Navigate to="/login" replace />

  return (
    <main className="min-h-screen px-4 pb-16 pt-28 sm:px-6">
      <div className="mx-auto max-w-2xl">
        <div className="rounded-3xl border border-violet-100 bg-white p-7 shadow-[0_18px_50px_-24px_rgba(124,58,237,0.28)]">
          <div className="flex items-center gap-4">
            <div className="grid size-14 place-items-center rounded-2xl bg-violet-100 text-violet-700">
              <UserRound size={28} />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-widest text-violet-600">Perfil</p>
              <h1 className="mt-1 text-2xl font-bold text-zinc-950">{user.name}</h1>
              <p className="text-sm text-zinc-500">{user.email}</p>
            </div>
          </div>

          <div className="mt-8 flex flex-col gap-3 sm:flex-row">
            <Link to="/nova-enquete" className="flex-1">
              <Button className="w-full"><Plus size={17} /> Nova enquete</Button>
            </Link>
            <Button variant="secondary" onClick={logout} className="flex-1">
              <LogOut size={17} /> Sair da conta
            </Button>
          </div>
        </div>
      </div>
    </main>
  )
}
