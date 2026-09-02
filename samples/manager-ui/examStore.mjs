export class ExamStore {
  constructor(api) {
    this.api = api;
    this.filters = { keyword: '', status: 'ALL' };
    this.page = 1;
    this.items = [];
    this.selected = null;
  }

  async search(filters = this.filters) {
    this.filters = { ...this.filters, ...filters };
    this.page = 1;
    return this.load();
  }

  async movePage(page) {
    if (page < 1) throw new Error('page must be positive');
    this.page = page;
    return this.load();
  }

  async load() {
    const response = await this.api.list({ ...this.filters, page: this.page });
    this.items = response.items;
    return response;
  }

  async uploadToRedis(planId) {
    await this.api.uploadToRedis(planId);
    this.selected = await this.api.detail(planId);
    return this.selected;
  }

  async removeFromRedis(planId) {
    await this.api.removeFromRedis(planId);
    this.selected = await this.api.detail(planId);
    return this.selected;
  }
}

