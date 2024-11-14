import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN'
import axios from 'axios'
import router from './router'
import store from './store'

Vue.use(ElementUI, { locale })
Vue.config.productionTip = false
Vue.prototype.$axios = axios;
window.eventBus = new Vue()

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
