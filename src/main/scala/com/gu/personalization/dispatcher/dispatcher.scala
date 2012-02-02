package com.gu.personalization.dispatcher

import org.scalatra.{ScalatraKernel, ScalatraFilter}

class Dispatcher extends ScalatraFilter with ScalatraKernel with Logging {

  get("/"){
    "HELLO WORLD"
  }
}

