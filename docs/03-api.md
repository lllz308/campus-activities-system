# API 文档

统一成功响应：`{"code":1,"message":"success","data":...}`。

统一失败响应：`{"code":0,"message":"错误说明","data":null}`，并使用对应 HTTP 状态码。

除公开接口外，请求头需携带 `Authorization: Bearer <token>`。

## 认证

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册，角色为 `STUDENT` 或 `TEACHER` |
| POST | `/api/auth/login` | 登录并返回 24 小时令牌 |
| POST | `/api/auth/logout` | 注销当前令牌 |

注册请求示例：

```json
{"username":"student02","password":"student123","displayName":"王同学","role":"STUDENT"}
```

## 活动公开查询

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/activities` | 查询已发布且尚未结束的活动 |
| GET | `/api/activities/{id}` | 查询已发布活动详情 |

## 教师活动管理

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/teacher/activities` | 查询自己创建的全部活动 |
| POST | `/api/teacher/activities` | 创建活动 |
| PUT | `/api/teacher/activities/{id}` | 修改活动 |
| DELETE | `/api/teacher/activities/{id}` | 删除尚无报名的活动 |

活动请求示例：

```json
{
  "title": "校园读书分享会",
  "description": "分享一本近期阅读的书。",
  "location": "图书馆报告厅",
  "startTime": "2026-10-10T14:00:00",
  "endTime": "2026-10-10T16:00:00",
  "registrationDeadline": "2026-10-09T18:00:00",
  "capacity": 50,
  "status": "PUBLISHED"
}
```

## 学生报名

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/student/activities/{id}/registrations` | 报名活动 |
| DELETE | `/api/student/activities/{id}/registrations` | 取消报名 |
| GET | `/api/student/registrations` | 查询自己的报名记录 |

