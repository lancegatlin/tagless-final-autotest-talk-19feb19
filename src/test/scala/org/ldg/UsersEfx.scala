package org.ldg

/**
  * An extension algebra/API for the Users[F] that allows directly
  * manipulating the underlying effect system.
  *
  * Implementations guarantee that:
  * Given user U
  *   Users.create(U) <-> efx_create(U)
  *   Users.remove(U) <-> efx_remove(U)
  *
  * (where <-> indicates same state/accumulation of effects)
  *
  * @tparam F
  */
trait UsersEfx[F[_]] { self:Users[F] =>
  // todo: make this rollback too?
  // the current state of the underlying mutable state
  def efx_state : F[List[Users.User]]
  
  // note: state could also include logging or anything else for demo keeping
  // it simple

  // functions that manipulate the underlying state directly
  // these can be thought of as the equivalent of a free monad's
  // effective case classes

  // create the effects of creating a user
  def efx_createUser(testUser: TestUser) : F[Unit]
  // create the effects of removing a user
  def efx_removeUser(testUser: TestUser) : F[Unit]
}
