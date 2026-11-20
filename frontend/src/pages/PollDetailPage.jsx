import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ArrowLeft, CheckCircle2 } from 'lucide-react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { Button } from '../components/Button'

export function PollDetailPage() {
  const { pollId } = useParams()
  const { user } = useAuth()
  const [poll, setPoll] = useState(null)
  const [results, setResults] = useState(null)
  const [selected, setSelected] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([api.getPoll(pollId), api.getResults(pollId)])
      .then(([pollData, resultsData]) => {
        setPoll(pollData)
        setResults(resultsData)
      })
      .catch((exception) => setError(exception.message))
      .finally(() => setLoading(false))
  }, [pollId])

  const vote = async () => {
    if (!user) return
    setError('')
    try {
      const updated = await api.vote(pollId, user.id, selected)
      setResults(updated)
    } catch (exception) {
      setError(exception.message)
    }
  }

  if (loading) return <main className="min-h-screen px-4 pt-28 text-sm text-zinc-500">Carregando enquete...</main>
  if (error && !poll) return <main className="min-h-screen px-4 pt-28"><p className="mx-auto max-w-2xl rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">{error}</p></main>

  return (
    <main className="min-h-screen px-4 pb-16 pt-28 sm:px-6">
      <div className="mx-auto max-w-3xl">
        <Link to="/" className="mb-5 inline-flex items-center gap-2 text-sm font-semibold text-zinc-500 hover:text-violet-700">
          <ArrowLeft size={16} /> Todas as enquetes
        </Link>

        <article className="rounded-3xl border border-violet-100 bg-white p-6 shadow-[0_18px_50px_-24px_rgba(124,58,237,0.28)] sm:p-9">
          <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ${poll.status === 'OPEN' ? 'bg-violet-50 text-violet-700' : 'bg-zinc-100 text-zinc-600'}`}>
            {poll.status === 'OPEN' ? 'Enquete aberta' : poll.status}
          </span>
          <h1 className="mt-4 text-3xl font-bold tracking-tight text-zinc-950">{poll.title}</h1>
          <p className="mt-2 text-zinc-500">{poll.question}</p>

          {poll.status === 'OPEN' && (
            <div className="mt-8 space-y-3">
              {poll.options.map((option) => (
                <button
                  key={option.id}
                  type="button"
                  onClick={() => setSelected(option.id)}
                  className={`flex w-full items-center justify-between rounded-xl border px-4 py-3.5 text-left text-sm font-semibold transition ${selected === option.id ? 'border-violet-500 bg-violet-50 text-violet-800 ring-4 ring-violet-100' : 'border-zinc-200 text-zinc-700 hover:border-violet-300'}`}
                >
                  {option.text}
                  {selected === option.id && <CheckCircle2 size={18} />}
                </button>
              ))}

              {user ? (
                <Button onClick={vote} disabled={!selected} className="mt-4 w-full">Confirmar voto</Button>
              ) : (
                <Link to="/login"><Button className="mt-4 w-full">Entrar para votar</Button></Link>
              )}
            </div>
          )}

          {error && <p className="mt-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{error}</p>}

          {results && (
            <div className="mt-8 border-t border-zinc-100 pt-7">
              <div className="mb-4 flex items-center justify-between">
                <h2 className="font-bold text-zinc-900">Resultados</h2>
                <span className="text-sm text-zinc-500">{results.totalVotes} votos</span>
              </div>
              <div className="space-y-4">
                {results.ranking.map((option) => (
                  <div key={option.optionId}>
                    <div className="mb-1.5 flex justify-between text-sm">
                      <span className="font-medium text-zinc-700">{option.text}</span>
                      <span className="font-semibold text-violet-700">{option.percentage}%</span>
                    </div>
                    <div className="h-2 overflow-hidden rounded-full bg-violet-100">
                      <div className="h-full rounded-full bg-violet-600" style={{ width: `${option.percentage}%` }} />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </article>
      </div>
    </main>
  )
}
