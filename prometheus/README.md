# Prometheus

expose all endpoint and add tags metrics 

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info,metric
  metrics:
    tags:
      application: ${spring.application.name}

```