import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import PokemonCard from './PokemonCard';

const mockPokemon = {
  id: 1,
  name: 'bulbasaur',
  imageUrl: 'https://example.com/1.png',
  types: ['grass', 'poison'],
  weight: 69,
  abilities: ['overgrow'],
};

describe('PokemonCard', () => {
  it('renders pokemon name', () => {
    render(
      <BrowserRouter>
        <PokemonCard pokemon={mockPokemon} />
      </BrowserRouter>
    );

    expect(screen.getByText('bulbasaur')).toBeInTheDocument();
  });

  it('renders pokemon id with padding', () => {
    render(
      <BrowserRouter>
        <PokemonCard pokemon={mockPokemon} />
      </BrowserRouter>
    );

    expect(screen.getByText('#001')).toBeInTheDocument();
  });

  it('renders pokemon types', () => {
    render(
      <BrowserRouter>
        <PokemonCard pokemon={mockPokemon} />
      </BrowserRouter>
    );

    expect(screen.getByText('grass')).toBeInTheDocument();
    expect(screen.getByText('poison')).toBeInTheDocument();
  });

  it('renders pokemon weight in kg', () => {
    render(
      <BrowserRouter>
        <PokemonCard pokemon={mockPokemon} />
      </BrowserRouter>
    );

    expect(screen.getByText('Weight: 6.9 kg')).toBeInTheDocument();
  });

  it('links to pokemon detail page', () => {
    render(
      <BrowserRouter>
        <PokemonCard pokemon={mockPokemon} />
      </BrowserRouter>
    );

    const link = screen.getByRole('link');
    expect(link).toHaveAttribute('href', '/pokemon/1');
  });
});
