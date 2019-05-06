/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package groovy.bugs

class Groovy9007Bug extends GroovyTestCase {
    void testProtectedFieldInEnum() {
        assertScript '''
            @groovy.transform.CompileStatic
            enum E {
                ONE(1), TWO(2)
                protected final int n
                E(int n) { this.n = n }
                static E valueOf(int n) {
                    values().find { it.n == n }
                }
            }

            assert E.valueOf(2).name() == 'TWO'
        '''
    }

    // GROOVY-8978
    void testProtectedFieldInChildWithExplicitThis() {
        assertScript '''
            import groovy.transform.CompileStatic

            @CompileStatic
            class LazyMap implements Map<String,Object> {
                @Delegate protected Map<String,Object> target
                LazyMap() {
                    target = new HashMap<>()
                }
            }

            @CompileStatic
            class TaskConfig extends LazyMap implements Cloneable {
                TaskConfig() {  }
                TaskConfig clone() {
                    def copy = (TaskConfig)super.clone()
                    copy.target = new HashMap<>(this.target)
                    return copy
                }
            }

            def t1 = new TaskConfig()
            t1.a = 'b'
            def t2 = t1.clone()
            assert t2.a == 'b'
        '''
    }
}
