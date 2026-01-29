import { useState, useEffect } from 'react';
import { pokemonApi } from '../services/api';

function LocalPokemon() {
  const [pokemon, setPokemon] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editing, setEditing] = useState(null);
  const [editForm, setEditForm] = useState({ name: '', weight: '', attributes: {} });

  useEffect(() => {
    fetchLocalPokemon();
  }, []);

  const fetchLocalPokemon = async () => {
    setLoading(true);
    try {
      const response = await pokemonApi.getLocalList();
      setPokemon(response.data);
    } catch (err) {
      setError(err.message || 'Failed to fetch local Pokemon');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (p) => {
    setEditing(p.id);
    setEditForm({
      name: p.name,
      weight: p.weight,
      attributes: { ...p.attributes },
    });
  };

  const handleSave = async (id) => {
    try {
      await pokemonApi.updateLocal(id, editForm);
      setEditing(null);
      fetchLocalPokemon();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const handleCancel = () => {
    setEditing(null);
    setEditForm({ name: '', weight: '', attributes: {} });
  };

  const handleAttributeChange = (key, value) => {
    setEditForm({
      ...editForm,
      attributes: { ...editForm.attributes, [key]: value },
    });
  };

  const addAttribute = () => {
    const key = prompt('Enter attribute name (e.g., name_jp):');
    if (key) {
      handleAttributeChange(key, '');
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="local-pokemon-page">
      <h1>My Collection</h1>

      {pokemon.length === 0 ? (
        <p className="empty-message">
          No Pokemon saved yet. Go to the Pokemon list and save some!
        </p>
      ) : (
        <div className="local-pokemon-list">
          {pokemon.map((p) => (
            <div key={p.id} className="local-pokemon-card">
              <img src={p.imageUrl} alt={p.name} />

              {editing === p.id ? (
                <div className="edit-form">
                  <input
                    type="text"
                    value={editForm.name}
                    onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                    placeholder="Name"
                  />
                  <input
                    type="number"
                    value={editForm.weight}
                    onChange={(e) => setEditForm({ ...editForm, weight: parseInt(e.target.value) || '' })}
                    placeholder="Weight"
                  />

                  <div className="attributes-section">
                    <h4>Custom Attributes</h4>
                    {Object.entries(editForm.attributes).map(([key, value]) => (
                      <div key={key} className="attribute-row">
                        <label>{key}:</label>
                        <input
                          type="text"
                          value={value}
                          onChange={(e) => handleAttributeChange(key, e.target.value)}
                        />
                      </div>
                    ))}
                    <button type="button" onClick={addAttribute} className="add-attr-btn">
                      + Add Attribute
                    </button>
                  </div>

                  <div className="edit-actions">
                    <button onClick={() => handleSave(p.id)}>Save</button>
                    <button onClick={handleCancel}>Cancel</button>
                  </div>
                </div>
              ) : (
                <div className="pokemon-info">
                  <h3>{p.name}</h3>
                  <p>Weight: {p.weight / 10} kg</p>
                  <p>Types: {p.types.join(', ')}</p>

                  {Object.keys(p.attributes).length > 0 && (
                    <div className="attributes">
                      <h4>Custom Attributes:</h4>
                      {Object.entries(p.attributes).map(([key, value]) => (
                        <p key={key}><strong>{key}:</strong> {value}</p>
                      ))}
                    </div>
                  )}

                  <button onClick={() => handleEdit(p)} className="edit-btn">
                    Edit
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default LocalPokemon;
