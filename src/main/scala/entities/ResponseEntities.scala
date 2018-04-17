package entities

/**
  * Base status.
  */
sealed trait Status

/**
  * OK status.
  */
case object OK extends Status

/**
  * ERROR status.
  */
case object ERROR extends Status

/**
  * Response entity.
  *
  * {{{
  *   val okResponse = ResponseEntity(yourMessage, OK)
  *   val errorResponse = ResponseEntity(yourMessage, ERROR)
  * }}}
  *
  * @param msg    Message.
  * @param status Status.
  */
case class ResponseEntity(msg: String, status: Status)