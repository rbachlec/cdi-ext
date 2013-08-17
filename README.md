cdi-ext is a small collection of CDI extensions and utils that I often need and use in my projects.

The main features are:
  - FactoryExtension: To be used if producer methods are not sufficient
  - DisposableSingleton scope: Like ApplicationScoped but beans are disposable manually
  - AnnotationUtil: Query annotations on AnnotatedType instances
  - BeanManagerUtil: Get contextual instances and inject things in unmanaged beans

Copyright 2013 Roland Bachlechner

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.