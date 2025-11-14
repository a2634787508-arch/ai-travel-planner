@echo off
REM 旅游规划AI系统 本地启动脚本
REM 作者: Travel AI Team
REM 版本: 1.0

echo 🚀 启动旅游规划AI系统...

REM 检查Java版本
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java 未安装或不在PATH中，请安装 Java 17 或更高版本
    pause
    exit /b 1
)

REM 检查jar文件是否存在
if not exist target\travel-ai-planner-0.0.1-SNAPSHOT.jar (
    echo ❌ 未找到应用jar文件，正在构建...
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ❌ 构建失败！
        pause
        exit /b 1
    )
)

REM 创建必要的目录
if not exist logs mkdir logs
if not exist uploads mkdir uploads

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=local
set JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC

REM 启动应用
echo 🌟 启动应用...
echo 📱 访问地址: http://localhost:8080
echo 🛑 按 Ctrl+C 停止应用
echo.

java %JAVA_OPTS% -jar target\travel-ai-planner-0.0.1-SNAPSHOT.jar

pause