import { Routes, Route } from 'react-router-dom'
import { Header } from './components/Header'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { ProfilePage } from './pages/ProfilePage'
import { CreatePollPage } from './pages/CreatePollPage'
import { PollDetailPage } from './pages/PollDetailPage'

function App() {
  return (
    <>
      <Header />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/perfil" element={<ProfilePage />} />
        <Route path="/nova-enquete" element={<CreatePollPage />} />
        <Route path="/polls/:pollId" element={<PollDetailPage />} />
      </Routes>
    </>
  )
}

export default App
