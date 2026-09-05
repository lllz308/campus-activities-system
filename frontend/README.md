# 校园活动管理桌面端

Vue 3 + electron-vite 桌面应用，对接项目根目录中的 Spring Boot REST API。

## 开发环境

- Node.js 20.19+ 或 22.12+
- pnpm 10+
- 后端运行在 `http://localhost:8080`

## 启动

```bash
pnpm install
pnpm dev
```

访问 `http://localhost:5173`。Vite 会把 `/api` 请求代理到后端，无需单独配置 CORS。

## Electron 启动与构建

```bash
pnpm electron:dev
pnpm electron:build
```

桌面端使用 Hash 路由，生产环境默认请求 `http://localhost:8080`；后端在其他地址时设置 `VITE_API_BASE_URL`。

## 功能

- 公开活动大厅、搜索、名额筛选和活动详情
- 学生登录、报名、取消报名和报名记录
- 教师登录、活动列表、新建、编辑、发布、取消与删除
- 用户注册、角色路由保护、登录态持久化和统一错误提示
- 桌面、平板和手机响应式布局

## 构建

```bash
pnpm build
```

构建结果位于 `dist/`。
