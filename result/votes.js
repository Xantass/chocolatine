function collectVotesFromResult(result) {
  const votes = { a: 0, b: 0, c: 0, d: 0 };

  result.rows.forEach(function (row) {
    votes[row.vote] = parseInt(row.count);
  });

  return votes;
}

module.exports = {
  collectVotesFromResult,
};

