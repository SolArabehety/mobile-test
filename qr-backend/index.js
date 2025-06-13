import express from 'express';
import crypto from 'crypto';

const app = express();
const port = process.env.PORT || 3000;
app.use(express.json());

const SEED_EXPIRATION_MS = 5 * 60 * 1000; // 5 minutes
const seeds = new Map(); // key: seed, value: { expiresAt, expired }


setInterval(() => {
  const now = Date.now();
  for (const [key, value] of seeds.entries()) {
    if (!value.expired && value.expiresAt <= now) {
      value.expired = true;
    }
  }
}, 60 * 1000);

// Función para generar una seed random
function generateSeed() {
  return crypto.randomBytes(16).toString('hex');
}

app.post('/seed', (req, res) => {
  const value = generateSeed();
  const expiresAt = Date.now() + SEED_EXPIRATION_MS;
  seeds.set(value, { expiresAt, expired: false });

  res.json({
    seed: value,
    expires_at: new Date(expiresAt).toISOString()
  });
});


app.post('/validate', (req, res) => {
  const seed = req.query.seed || req.body.seed;
  const seedData = seeds.get(seed);

  if (!seedData) {
    return res.status(200).json({ valid: false, reason: 'Seed not found' });
  }

  if (seedData.expired) {
    return res.status(200).json({ valid: false, reason: 'Seed expired' });
  }

  res.json({ valid: true });
});


app.listen(port, '0.0.0.0', () => {
  if (process.env.NODE_ENV !== 'production') {
    console.log(`Server running at http://localhost:${port}`);
  }
});
