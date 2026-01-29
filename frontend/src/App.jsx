import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home';
import Detail from './pages/Detail';
import LocalPokemon from './pages/LocalPokemon';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <nav className="navbar">
          <Link to="/" className="nav-logo">Pokemon API</Link>
          <div className="nav-links">
            <Link to="/">Browse</Link>
            <Link to="/collection">My Collection</Link>
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/pokemon/:id" element={<Detail />} />
            <Route path="/collection" element={<LocalPokemon />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
