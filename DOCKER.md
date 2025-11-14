# 🐳 Docker 部署指南

## 📋 前置要求

1. **安装 Docker Desktop**
   - 下载地址：https://www.docker.com/products/docker-desktop
   - 安装后启动 Docker Desktop
   - 确保 Docker 正在运行

2. **安装 docker-compose**（Docker Desktop 已包含）

## 🚀 快速部署

### 方法一：使用构建脚本（推荐）

**Windows 用户：**
```bash
# 运行 Windows 构建脚本
build-docker.bat
```

**Linux/Mac 用户：**
```bash
# 给脚本执行权限
chmod +x build-docker.sh

# 运行构建脚本
./build-docker.sh
```

### 方法二：手动部署

1. **配置环境变量**
   ```bash
   # 复制环境变量模板
   cp .env.example .env
   
   # 编辑 .env 文件，填入您的阿里云百炼API密钥
   # DASHSCOPE_API_KEY=your_actual_api_key_here
   ```

2. **构建镜像**
   ```bash
   docker build -t travel-ai-planner:latest .
   ```

3. **启动服务**
   ```bash
   docker-compose up -d
   ```

## 📁 项目结构

```
├── Dockerfile              # Docker 镜像构建文件
├── docker-compose.yml      # 容器编排配置
├── .dockerignore          # Docker 构建忽略文件
├── .env.example           # 环境变量模板
├── build-docker.sh        # Linux/Mac 构建脚本
├── build-docker.bat       # Windows 构建脚本
├── docker/
│   └── mysql/
│       └── init.sql       # 数据库初始化脚本
└── src/main/resources/
    └── application-docker.yml  # Docker 环境配置
```

## 🔧 配置说明

### 环境变量配置 (.env)

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DASHSCOPE_API_KEY` | 阿里云百炼API密钥 | 必填 |
| `MYSQL_ROOT_PASSWORD` | MySQL root密码 | root123456 |
| `MYSQL_DATABASE` | 数据库名称 | travel_ai |
| `MYSQL_USER` | 数据库用户名 | travel_user |
| `MYSQL_PASSWORD` | 数据库密码 | travel_pass |
| `JWT_SECRET` | JWT密钥 | travelAiSecretKey2024... |

### 端口映射

| 服务 | 容器端口 | 主机端口 |
|------|----------|----------|
| Spring Boot应用 | 8080 | 8080 |
| MySQL数据库 | 3306 | 3306 |

## 🌐 访问地址

- **应用首页**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/actuator/health

## 📊 常用命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 查看应用日志
docker-compose logs -f app

# 查看数据库日志
docker-compose logs -f mysql

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 停止服务并删除数据卷
docker-compose down -v

# 重新构建并启动
docker-compose up -d --build

# 进入应用容器
docker-compose exec app bash

# 进入数据库容器
docker-compose exec mysql mysql -u travel_user -p travel_ai
```

## 🔍 故障排除

### 1. 端口冲突
如果8080或3306端口被占用，修改 `docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "8081:8080"  # 将主机端口改为8081
  - "3307:3306"  # 将主机端口改为3307
```

### 2. 内存不足
如果出现内存不足错误，调整JVM参数：
```yaml
environment:
  JAVA_OPTS: "-Xmx256m -Xms128m -XX:+UseG1GC"
```

### 3. 数据库连接失败
- 确保数据库容器正常启动：`docker-compose logs mysql`
- 检查数据库连接配置
- 等待数据库完全启动后再启动应用

### 4. AI功能不可用
- 检查 `.env` 文件中的 `DASHSCOPE_API_KEY` 是否正确
- 确认API密钥有效且有足够额度

## 📈 监控和维护

### 健康检查
```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health

# 检查容器健康状态
docker-compose ps
```

### 日志管理
```bash
# 清理日志
docker-compose logs --no-log-prefix > /dev/null 2>&1

# 日志轮转（在docker-compose.yml中配置）
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

### 备份数据
```bash
# 备份数据库
docker-compose exec mysql mysqldump -u root -p travel_ai > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -u root -p travel_ai < backup.sql
```

## 🔄 更新部署

```bash
# 拉取最新代码
git pull

# 重新构建镜像
docker-compose build --no-cache

# 重启服务
docker-compose up -d
```

## 🏗️ 生产环境部署

对于生产环境，建议：

1. **使用外部数据库**：不要使用容器内的MySQL
2. **配置HTTPS**：使用反向代理（如Nginx）
3. **设置资源限制**：在docker-compose.yml中配置内存和CPU限制
4. **使用环境变量管理敏感信息**：不要在代码中硬编码密钥
5. **配置日志收集**：使用ELK或其他日志系统
6. **设置监控告警**：使用Prometheus + Grafana

## 📞 技术支持

如果遇到问题，请：

1. 检查日志：`docker-compose logs`
2. 确认配置：检查 `.env` 文件
3. 验证网络：确保端口可访问
4. 查看资源：`docker stats`

---

**注意**: 首次启动可能需要较长时间下载依赖，请耐心等待。