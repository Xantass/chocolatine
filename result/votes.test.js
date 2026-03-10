const { collectVotesFromResult } = require('./votes');

describe('collectVotesFromResult', () => {
  test('retourne 0 pour tous les choix quand aucun vote', () => {
    const result = { rows: [] };

    const votes = collectVotesFromResult(result);

    expect(votes).toEqual({ a: 0, b: 0, c: 0, d: 0 });
  });

  test('agrège correctement les résultats retournés par la base', () => {
    const result = {
      rows: [
        { vote: 'a', count: '2' },
        { vote: 'c', count: '5' },
      ],
    };

    const votes = collectVotesFromResult(result);

    expect(votes).toEqual({ a: 2, b: 0, c: 5, d: 0 });
  });
});

