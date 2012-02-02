package com.gu.personalization
package dispatcher

import org.scalatra.{ScalatraKernel, ScalatraFilter}

class Dispatcher extends ScalatraFilter with ScalatraKernel with Logging {

  get("/"){
    "HELLO WORLD"
  }


  post("/saveProfile") {
    val userId = params.getOrElse("userId", throw new Exception("missing userid"))
    val terms = params.getOrElse("terms","")

    model.Personalization.save(userId, terms)
  }
}

