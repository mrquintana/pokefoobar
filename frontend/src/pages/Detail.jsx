import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { pokemonApi } from '../services/api';

function Detail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [pokemon, setPokemon] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [copying, setCopying] = useState(false);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    const fetchPokemon = async () => {
      setLoading(true);
      try {
        const response = await pokemonApi.getDetail(id);
        setPokemon(response.data);
      } catch (err) {
        setError(err.message || 'Failed to fetch Pokemon');
      } finally {
        setLoading(false);
      }
    };

    fetchPokemon();
  }, [id]);

  const handleCopyToLocal = async () => {
    setCopying(true);
    try {
      await pokemonApi.copyToLocal(id);
      setCopied(true);
    } catch (err) {
      if (err.response?.status === 400) {
        setCopied(true);
      } else {
        setError(err.message);
      }
    } finally {
      setCopying(false);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">Error: {error}</div>;
  if (!pokemon) return <div className="error">Pokemon not found</div>;

  return (
    <div className="detail-page">
      <button onClick={() => navigate(-1)} className="back-button">
        Back
      </button>

      <div className="pokemon-detail">
        <div className="detail-header">
          <img src={pokemon.imageUrl} alt={pokemon.name} className="detail-image" />
          <div className="detail-info">
            <span className="pokemon-id">#{pokemon.id.toString().padStart(3, '0')}</span>
            <h1>{pokemon.name}</h1>
            <div className="types">
              {pokemon.types.map((type) => (
                <span key={type} className={`type-badge ${type}`}>{type}</span>
              ))}
            </div>
          </div>
        </div>

        <div className="detail-section">
          <h2>Description</h2>
          <p>{pokemon.description}</p>
        </div>

        <div className="detail-section">
          <h2>Stats</h2>
          <p>Weight: {pokemon.weight / 10} kg</p>
          <p>Abilities: {pokemon.abilities.join(', ')}</p>
        </div>

        <div className="detail-section">
          <h2>Evolutions</h2>
          <div className="evolutions">
            {pokemon.evolutions.map((evo) => (
              <div
                key={evo.id}
                className={`evolution-item ${evo.id === pokemon.id ? 'current' : ''}`}
                onClick={() => navigate(`/pokemon/${evo.id}`)}
              >
                <img src={evo.imageUrl} alt={evo.name} />
                <span>{evo.name}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="detail-actions">
          <button
            onClick={handleCopyToLocal}
            disabled={copying || copied}
            className="copy-button"
          >
            {copied ? 'Saved to Collection' : copying ? 'Saving...' : 'Save to Collection'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default Detail;
