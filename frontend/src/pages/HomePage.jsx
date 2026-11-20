import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ArrowDown, Plus } from 'lucide-react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { Button } from '../components/Button'
import { PollGrid } from '../components/PollGrid'

export function HomePage() {
  const [polls, setPolls] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const { user } = useAuth()

  useEffect(() => {
    api.getPolls()
      .then(setPolls)
      .catch((exception) => setError(exception.message))
      .finally(() => setLoading(false))
  }, [])

  return (
    <>
      <section className="relative overflow-hidden bg-white px-4 pb-16 pt-32 sm:px-6 sm:pb-24 sm:pt-40">
        <div className="absolute left-1/2 top-16 size-[420px] -translate-x-1/2 rounded-full bg-violet-100/60 blur-3xl" />
        <div className="relative mx-auto max-w-6xl text-center">
          <span className="inline-flex rounded-full border border-violet-200 bg-violet-50 px-3 py-1 text-xs font-bold uppercase tracking-[0.18em] text-violet-700">
            Opiniões em movimento
          </span>
          <h1 className="mx-auto mt-6 max-w-3xl text-4xl font-black tracking-tight text-zinc-950 sm:text-6xl">
            Decida juntos.<br />
            <span className="text-violet-600">Compartilhe sua escolha.</span>
          </h1>
          <p className="mx-auto mt-5 max-w-xl text-base leading-7 text-zinc-500 sm:text-lg">
            Crie enquetes, participe das conversas e acompanhe os resultados em tempo real.
          </p>
          <div className="mt-8 flex flex-col justify-center gap-3 sm:flex-row">
            <Button onClick={() => document.getElementById('enquetes')?.scrollIntoView()} variant="primary">
              Ver enquetes <ArrowDown size={17} />
            </Button>
            {user && (
              <Link to="/nova-enquete">
                <Button variant="secondary" className="w-full sm:w-auto">
                  <Plus size={17} /> Criar enquete
                </Button>
              </Link>
            )}
          </div>
        </div>
      </section>

      <section id="enquetes" className="scroll-mt-20 border-t border-violet-100 bg-zinc-50/70 px-4 py-14 sm:px-6 sm:py-20">
        <div className="mx-auto max-w-6xl">
          <div className="mb-8 flex items-end justify-between">
            <div>
              <p className="text-sm font-semibold text-violet-600">Comunidade</p>
              <h2 className="mt-1 text-2xl font-bold text-zinc-950 sm:text-3xl">Enquetes recentes</h2>
            </div>
            {user && (
              <Link to="/nova-enquete" className="hidden text-sm font-semibold text-violet-600 sm:block">
                Criar nova
              </Link>
            )}
          </div>
          {loading && <p className="text-sm text-zinc-500">Carregando enquetes...</p>}
          {error && <p className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">{error}</p>}
          {!loading && !error && <PollGrid polls={polls} />}
        </div>
      </section>
    </>
  )
}
