/*
 * Copyright 2010 TRICREO, Inc. (http://tricreo.jp/)
 * Copyright 2011 Sisioh Project and others. (http://www.sisioh.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.sisioh.dddbase.core.model

/**
 * エンティティの識別子を表すトレイト。
 *
 * エンティティで用いられる識別子を型として表現することを目的としている。
 *
 * @tparam A 識別子の値を表す型
 */
trait Identity[+A] extends Serializable {

  /**
   * 識別子の値を取得する。
   *
   * @return 識別子の値
   */
  def value: A

}

/**
 * 順序をサポートした[[org.sisioh.dddbase.core.model.Identity]]。
 *
 * @tparam A 識別子の値を表す型
 * @tparam ID 識別子の型
 */
trait OrderedIdentity[A, ID <: Identity[A]]
  extends Identity[A] with Ordered[ID]

/**
 * [[org.sisioh.dddbase.core.model.OrderedIdentity]]の骨格実装。
 *
 * @tparam A 識別子の値を表す型
 * @tparam ID 識別子の型
 */
abstract class AbstractOrderedIdentity[A <% Ordered[A], ID <: Identity[A]]
  extends OrderedIdentity[A, ID] {

  def compare(that: ID): Int = {
    value compare that.value
  }

}

/**
 * 識別子の値が空だった場合の例外。
 */
case class EmptyIdentityException() extends Exception

/**
 * 空の識別子を表す値オブジェクト。
 */
private[core]
class EmptyIdentity
  extends Identity[Nothing] {

  def value = throw EmptyIdentityException()

  override def equals(obj: Any): Boolean = obj match {
    case that: EmptyIdentity => this eq that
    case _ => false
  }

  override def hashCode(): Int = 31 * 1

  override def toString = "EmptyIdentity"
}

object EmptyIdentity extends EmptyIdentity

private[core]
class IdentityImpl[A](val value: A)
  extends Identity[A] {

  override def equals(obj: Any) = obj match {
    case that: EmptyIdentity => false
    case that: Identity[_] =>
      value == that.value
    case _ => false
  }

  override def hashCode = 31 * value.##

  override def toString = s"Identity($value)"

}

/**
 * コンパニオンオブジェクト。
 */
object Identity {

  /**
   * [[org.sisioh.dddbase.core.model.Identity]]を生成する。
   *
   * @param value 識別子の値
   * @tparam A 識別子の値の型
   * @return [[org.sisioh.dddbase.core.model.Identity]]
   */
  def apply[A](value: A): Identity[A] = new IdentityImpl(value)

  /**
   * 空の[[org.sisioh.dddbase.core.model.Identity]]を返す。
   *
   * @return [[org.sisioh.dddbase.core.model.Identity]]
   */
  def empty[A]: Identity[A] = EmptyIdentity

  /**
   * 抽出子メソッド。
   *
   * @param v [[org.sisioh.dddbase.core.model.Identity]]
   * @tparam A 識別子の値の型
   * @return 識別子の値
   */
  def unapply[A](v: Identity[A]): Option[A] = Some(v.value)

}



