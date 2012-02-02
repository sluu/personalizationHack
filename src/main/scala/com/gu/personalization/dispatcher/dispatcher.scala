package com.gu.personalization.dispatcher

import org.scalatra.{ScalatraKernel, ScalatraFilter}
import com.gu.personalization.api.Api

class Dispatcher extends ScalatraFilter with ScalatraKernel with Logging {

  get("/"){
    "HELLO WORLD"
  }

  post("/profile/%s/createOrUpdatePersonalisation") {
      val userProfileId = params.get("userProfileId").map(java.lang.Long.parseLong(_))
      Api.Personalisation.save(userProfileId, params)

    }
}

