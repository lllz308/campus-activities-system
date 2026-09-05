# 软件设计

## 架构

```text
HTTP Client
    |
Controller  - 参数校验、统一响应
    |
Service     - 权限与业务规则、事务边界
    |
Repository  - JPA 数据访问
    |
H2 / MySQL-compatible database
```

安全过滤器从 `Authorization: Bearer <token>` 读取令牌，只使用 SHA-256 哈希查询会话。认证成功后将用户 ID、角色和显示名放入 Spring Security 上下文。

## 核心数据关系

```text
User (teacher) 1 ----- N Activity
User (student) 1 ----- N Registration N ----- 1 Activity
User           1 ----- N AuthToken
```

`Registration` 对 `(activity_id, student_id)` 建立唯一约束，数据库约束与服务层重复报名检查共同保证一致性。

## 关键报名流程

1. 验证请求用户是学生。
2. 对活动记录加悲观写锁。
3. 检查活动存在且状态为已发布。
4. 检查报名截止时间。
5. 检查是否重复报名。
6. 检查当前人数是否达到容量。
7. 创建报名记录并提交事务。

## 包结构

| 包 | 职责 |
| --- | --- |
| `auth` | 注册、登录、登出 |
| `security` | 令牌、认证过滤器、安全规则 |
| `user` | 用户与角色 |
| `activity` | 活动实体、管理和查询 |
| `registration` | 学生报名与取消 |
| `common` | 统一响应和异常处理 |
| `config` | 本地演示数据 |

