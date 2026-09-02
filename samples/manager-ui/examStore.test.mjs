import assert from 'node:assert/strict';
import test from 'node:test';
import { ExamStore } from './examStore.mjs';

test('search resets page and keeps filters', async () => {
  const calls = [];
  const store = new ExamStore({
    list: async query => { calls.push(query); return { items: [] }; }
  });
  store.page = 3;

  await store.search({ keyword: 'sample' });

  assert.equal(store.page, 1);
  assert.deepEqual(calls[0], { keyword: 'sample', status: 'ALL', page: 1 });
});

test('redis command reloads selected detail', async () => {
  let uploaded = false;
  const store = new ExamStore({
    uploadToRedis: async () => { uploaded = true; },
    detail: async planId => ({ planId, redisReady: uploaded })
  });

  const detail = await store.uploadToRedis('plan-1');

  assert.equal(detail.redisReady, true);
  assert.equal(store.selected.planId, 'plan-1');
});

