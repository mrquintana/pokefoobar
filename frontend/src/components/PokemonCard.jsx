import { Link } from 'react-router-dom';

function PokemonCard({ pokemon }) {
  const typeColors = {
    grass: '#78C850',
    poison: '#A040A0',
    fire: '#F08030',
    water: '#6890F0',
    electric: '#F8D030',
    normal: '#A8A878',
    fighting: '#C03028',
    ground: '#E0C068',
    rock: '#B8A038',
    bug: '#A8B820',
    ghost: '#705898',
    psychic: '#F85888',
    ice: '#98D8D8',
    dragon: '#7038F8',
    dark: '#705848',
    steel: '#B8B8D0',
    fairy: '#EE99AC',
    flying: '#A890F0',
  };

  return (
    <Link to={`/pokemon/${pokemon.id}`} className="pokemon-card">
      <div className="pokemon-image">
        <img src={pokemon.imageUrl} alt={pokemon.name} />
      </div>
      <div className="pokemon-info">
        <span className="pokemon-id">#{pokemon.id.toString().padStart(3, '0')}</span>
        <h3 className="pokemon-name">{pokemon.name}</h3>
        <div className="pokemon-types">
          {pokemon.types.map((type) => (
            <span
              key={type}
              className="type-badge"
              style={{ backgroundColor: typeColors[type] || '#777' }}
            >
              {type}
            </span>
          ))}
        </div>
        <p className="pokemon-weight">Weight: {pokemon.weight / 10} kg</p>
      </div>
    </Link>
  );
}

export default PokemonCard;
