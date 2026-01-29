import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import PokemonList from './PokemonList';

const mockPokemon = [
  {
    id: 1,
    name: 'bulbasaur',
    imageUrl: 'https://example.com/1.png',
    types: ['grass'],
    weight: 69,
    abilities: ['overgrow'],
  },
  {
    id: 2,
    name: 'ivysaur',
    imageUrl: 'https://example.com/2.png',
    types: ['grass'],
    weight: 130,
    abilities: ['overgrow'],
  },
];

describe('PokemonList', () => {
  it('renders loading state', () => {
    render(<PokemonList pokemon={[]} loading={true} error={null} />);
    expect(screen.getByText('Loading Pokemon...')).toBeInTheDocument();
  });

  it('renders error state', () => {
    render(<PokemonList pokemon={[]} loading={false} error="Network error" />);
    expect(screen.getByText('Error: Network error')).toBeInTheDocument();
  });

  it('renders empty state', () => {
    render(<PokemonList pokemon={[]} loading={false} error={null} />);
    expect(screen.getByText('No Pokemon found')).toBeInTheDocument();
  });

  it('renders pokemon cards', () => {
    render(
      <BrowserRouter>
        <PokemonList pokemon={mockPokemon} loading={false} error={null} />
      </BrowserRouter>
    );

    expect(screen.getByText('bulbasaur')).toBeInTheDocument();
    expect(screen.getByText('ivysaur')).toBeInTheDocument();
  });
});
