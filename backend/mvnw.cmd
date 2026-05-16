@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM   http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "__MVNW_ARG0_NAME__=%~nx0")
@SET ___MVNW_LOCATION=%~dp0

@SET MAVEN_PROJECTBASEDIR=%___MVNW_LOCATION%

@SET MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

@FOR /F "usebackq tokens=1,* delims==" %%A IN ("%MAVEN_WRAPPER_PROPERTIES%") DO (
  @IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@FOR /F "tokens=* delims=" %%i IN ('echo %DISTRIBUTION_URL%') DO SET DISTRIBUTION_URL=%%i

@SET MAVEN_VERSION=
@FOR /F "tokens=4 delims=-" %%A IN ("%DISTRIBUTION_URL%") DO (
  @FOR /F "tokens=1 delims=." %%B IN ("%%A") DO SET MAVEN_VERSION=%%A
)

@IF "%MAVEN_USER_HOME%"=="" (
  SET "MAVEN_USER_HOME=%USERPROFILE%\.m2"
)

@SET "MAVEN_HOME=%MAVEN_USER_HOME%\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin\apache-maven-%MAVEN_VERSION%"

@IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  @echo Downloading Apache Maven %MAVEN_VERSION%...
  @MKDIR "%MAVEN_USER_HOME%\wrapper\dists" 2>NUL
  @SET "TMP_ZIP=%TEMP%\maven-wrapper-download.zip"
  @powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%DISTRIBUTION_URL%', '%MAVEN_USER_HOME%\wrapper\dists\maven.zip') }"
  @powershell -Command "& { Add-Type -Assembly System.IO.Compression.FileSystem; [IO.Compression.ZipFile]::ExtractToDirectory('%MAVEN_USER_HOME%\wrapper\dists\maven.zip', '%MAVEN_USER_HOME%\wrapper\dists') }"
  @DEL "%MAVEN_USER_HOME%\wrapper\dists\maven.zip"
  @echo Maven %MAVEN_VERSION% downloaded.
)

@"%MAVEN_HOME%\bin\mvn.cmd" %*
