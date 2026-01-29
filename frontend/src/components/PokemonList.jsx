import PokemonCard from './PokemonCard';

function PokemonList({ pokemon, loading, error }) {
  if (loading) {
    return <div className="loading">Loading Pokemon...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!pokemon || pokemon.length === 0) {
    return <div className="empty">No Pokemon found</div>;
  }

  return (
    <div className="pokemon-grid">
      {pokemon.map((p) => (
        <PokemonCard key={p.id} pokemon={p} />
      ))}
    </div>
  );
}

export default PokemonList;
