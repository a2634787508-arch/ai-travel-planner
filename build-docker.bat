@echo off
REM 旅游规划AI系统 Docker 构建脚本 (Windows版本)
REM 作者: Travel AI Team
REM 版本: 1.0

echo 🚀 开始构建旅游规划AI系统 Docker 镜像...

REM 检查Docker是否安装
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker 未安装，请先安装 Docker
    pause
    exit /b 1
)

REM 检查docker-compose是否安装
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ docker-compose 未安装，请先安装 docker-compose
    pause
    exit /b 1
)

REM 创建必要的目录
echo 📁 创建必要的目录...
if not exist logs mkdir logs
if not exist uploads mkdir uploads
if not exist docker\mysql mkdir docker\mysql

REM 检查环境变量文件
if not exist .env (
    echo ⚠️  未找到 .env 文件，创建示例文件...
    (
        echo # 阿里云百炼API密钥 ^(请替换为您的实际密钥^)
        echo DASHSCOPE_API_KEY=your_dashscope_api_key_here
        echo.
        echo # 数据库配置
        echo MYSQL_ROOT_PASSWORD=root123456
        echo MYSQL_DATABASE=travel_ai
        echo MYSQL_USER=travel_user
        echo MYSQL_PASSWORD=travel_pass
        echo.
        echo # 应用配置
        echo SPRING_PROFILES_ACTIVE=docker
        echo JWT_SECRET=travelAiSecretKey2024ForJwtTokenGeneration
    ) > .env
    echo ⚠️  请编辑 .env 文件，填入您的实际配置
)

REM 构建镜像
echo 🔨 构建 Docker 镜像...
docker build -t travel-ai-planner:latest .

if %errorlevel% equ 0 (
    echo ✅ Docker 镜像构建成功！
) else (
    echo ❌ Docker 镜像构建失败！
    pause
    exit /b 1
)

REM 询问是否启动服务
set /p response="🤔 是否要启动服务？(y/n): "
if /i "%response%"=="y" (
    echo 🚀 启动服务...
    docker-compose up -d
    
    if %errorlevel% equ 0 (
        echo ✅ 服务启动成功！
        echo 📱 应用访问地址: http://localhost:8080
        echo 🗄️  数据库连接: localhost:3306
        echo 📊 查看服务状态: docker-compose ps
        echo 📝 查看日志: docker-compose logs -f
        echo 🛑 停止服务: docker-compose down
    ) else (
        echo ❌ 服务启动失败！
        pause
        exit /b 1
    )
)

echo 🎉 构建完成！
pause