import { useState, useEffect } from 'react';
import { pokemonApi } from '../services/api';
import PokemonList from '../components/PokemonList';
import Pagination from '../components/Pagination';

function Home() {
  const [pokemon, setPokemon] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);
  const size = 20;

  useEffect(() => {
    const fetchPokemon = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await pokemonApi.list(page, size);
        setPokemon(response.data.content);
        setTotalCount(response.data.totalCount);
      } catch (err) {
        setError(err.message || 'Failed to fetch Pokemon');
      } finally {
        setLoading(false);
      }
    };

    fetchPokemon();
  }, [page]);

  return (
    <div className="home-page">
      <h1>Pokemon List</h1>
      <PokemonList pokemon={pokemon} loading={loading} error={error} />
      {!loading && !error && (
        <Pagination
          page={page}
          totalCount={totalCount}
          size={size}
          onPageChange={setPage}
        />
      )}
    </div>
  );
}

export default Home;
