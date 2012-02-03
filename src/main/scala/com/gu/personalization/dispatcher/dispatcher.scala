package com.gu.personalization
package dispatcher

import org.scalatra.{ScalatraKernel, ScalatraFilter}
import com.gu.openplatform.contentapi.Api
import scala.collection.JavaConversions._
import com.gu.openplatform.contentapi.model.Content
import com.gu.openplatform.contentapi.connection.JavaNetHttp
import org.scalatra.scalate.ScalateSupport

class Dispatcher extends ScalatraFilter with ScalateSupport with Logging {

  get("/"){
    "HELLO WORLD"
  }


  post("/saveProfile") {
    val userId = params.getOrElse("userId", throw new Exception("missing userid"))
    val terms = multiParams.get("terms").getOrElse(Seq()).mkString(",").toLowerCase

    model.Personalization.save(userId, terms)
  }

  get("/personalization/:userId"){
    val userId = params.getOrElse("userId", throw new Exception("missing userid"))
    val terms = model.Personalization.get(userId).split(",")
    try{
      val tags = terms.map(ApiClient.getTagId(_)).mkString("|")
      val content = ApiClient.searchApi(tags)
      val renderParams = Map("content" -> content, "userid" -> userId)
      render("templates/personalizedPage", renderParams)
      // renderTemplate("templates/personalizedPage.ssp", "content" -> content, "userid" -> userId)

    }catch{
      case e => {
        log.error(e.getMessage)
        log.error(e.getStackTraceString)
        terms
      }
    }
  }


  def render(file: String, renderParams: Map[String, Any] = Map(), contentTypeHeader: String = "text/html;charset=UTF-8", cacheMaxAge: Int = 0) = {
    response.setHeader("Cache-Control", "public, max-age=%d" format cacheMaxAge)
    contentType = contentTypeHeader
    //templateEngine.layout("/WEB-INF/scalate/%s.ssp" format file, renderParams)
    val htmlString = "<html><head><title></title></head><body>" +
      renderParams.get("content").get.asInstanceOf[List[com.gu.openplatform.contentapi.model.Content]].map(_.webTitle).mkString(" <br> ") +
      "</body></html>"
    htmlString
  }

}


object ApiClient extends Api with JavaNetHttp with Logging  {

  apiKey = Some("techdev-internal")

  def searchApi(tagstring : String) : List[Content]  = {

    log.info(tagstring)
    val apiRequest = Api.search
                      .tag(tagstring)
                      .showFields("all")

    apiRequest.results

  }

  def getTagId(term : String) : String = {
    val apiRequest = Api.tags
                        .tagType(term)
    val firstResult : Option[com.gu.openplatform.contentapi.model.Tag] = apiRequest.results.headOption
    firstResult.map(_.id).getOrElse("")
  }

}
