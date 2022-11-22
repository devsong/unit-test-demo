# spring test demo project([chinese version](readme_zh_cn.md))
a simple project for test/TDD using mockito,this project cotains several examples about mockito/spring/springboot test 

## Used tech
 - base tech:java 11  springboot springmvc jpa lombok mapstruct mariaDB4j
 - buildtool: java11 gradle

## Package Description(src/main/java)
- client: third party system,contains client called ```PaymentClient``` which incates payment system
- common: common module
- config: spring boot app/bean config
- controller: based on springmvc,contains three simple api for demonstration
    - ```GET  /user/info/{userId}``` query user info by userId
    - ```POST /user``` create one user
    - ```GET  /user/query-by-username``` query user info by username
- dto: DTO object for modules and system interface
- entity: persistence object for jdbc
- service: user service logic,contains business logic
- repository: jpa interface for system data storge
- ms: mapstruct mapper

## Package Description(src/test/java)
- benchmark: contains parallels request in api/service test case
- config: test config
- integrationtest: integration test example for TDD
- testdouble: examples show testdouble object
- unittest: unit test example for TDD
- util: utils class
- IntegrationTestBase: base class for integration test class
- TruncateDatabaseService: clean up database data for test purpose

## A simple png for describe system arch

![user system arch](user_system_arch.png)
