# 校园活动管理系统 V1.0

基于 Spring Boot 的校园活动管理 REST API。项目依据《实验一：基于工程意图的软件迭代开发》中的业务背景，将 V1.0 收敛为一条可运行、可验证的核心业务链路。

项目同时包含 `frontend/` Vue 3 单页应用，可直接使用 VS Code 打开项目根目录并分别启动前后端。

## 工程意图

- 目标：让教师能够发布校园活动，让学生能够查看并报名活动。
- 范围：注册、登录、公开活动查询、教师活动管理、学生报名和取消报名。
- 约束：教师只能管理自己的活动；学生不能重复报名；截止后、取消或满员的活动不能报名；密码只保存 BCrypt 哈希；登录令牌只保存 SHA-256 哈希。
- 完成依据：应用可启动，接口集成测试通过，教师发布到学生报名的基本业务链路可执行。

## 技术栈

- Java 21
- Spring Boot 3.5.7
- Spring Web / Validation / Security
- Spring Data JPA
- H2 内存数据库
- Maven

## 快速开始

环境要求：JDK 21+、Maven 3.9+。

```bash
mvn spring-boot:run
```

服务地址：`http://localhost:8080`

前端启动：

```bash
cd frontend
pnpm install
pnpm dev
```

前端地址：`http://localhost:5173`

H2 控制台：`http://localhost:8080/h2-console`

- JDBC URL：`jdbc:h2:mem:campus`
- 用户名：`sa`
- 密码：留空

演示账号：

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 教师 | `teacher01` | `teacher123` |
| 学生 | `student01` | `student123` |

演示账号只用于本地实验，部署前应删除 `DemoDataConfig`。

## 验证

```bash
mvn test
```

测试覆盖应用上下文启动、公开活动查询、学生登录和报名主流程。

## 文档

- [需求与工程意图](docs/01-requirements-and-intent.md)
- [软件设计](docs/02-design.md)
- [API 文档](docs/03-api.md)
- [验证方案](docs/04-verification.md)

## Git 建议

项目已提供 `.gitignore`。请从首次开发开始逐步提交，建议按“项目骨架、认证、活动管理、报名规则、测试与文档”拆分真实提交，不要在实验结束时一次性补造历史。
