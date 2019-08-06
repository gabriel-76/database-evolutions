package com.evolutions.database.api

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.annotation.meta.beanSetter
import scala.beans.BeanProperty

@Service
class DbScalaConfig () (
//class DbScalaConfig @Autowired() (
//  @(Value @beanSetter)("{spring.datasource.driverClassName}") @BeanProperty var driverClassName: String,
//  @(Value @beanSetter)("{spring.datasource.url}") @BeanProperty var url: String,
//  @(Value @beanSetter)("{spring.datasource.username}") @BeanProperty var username: String,
//  @(Value @beanSetter)("{spring.datasource.password}") @BeanProperty var password: String,
) {

//  def getUrl: String = url
//
//  def getDriverClassName: String = driverClassName
//
//  def getUsername: String = username
//
//  def getPassword: String = password

}
