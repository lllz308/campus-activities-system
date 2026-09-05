import { createRouter, createWebHashHistory } from 'vue-router'
import { session } from './session'
import ActivitiesView from './views/ActivitiesView.vue'
import ActivityDetailView from './views/ActivityDetailView.vue'
import LoginView from './views/LoginView.vue'
import RegisterView from './views/RegisterView.vue'
import StudentRegistrationsView from './views/StudentRegistrationsView.vue'
import TeacherActivitiesView from './views/TeacherActivitiesView.vue'
import ActivityEditorView from './views/ActivityEditorView.vue'
import NotFoundView from './views/NotFoundView.vue'

const router = createRouter({
  history: createWebHashHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    { path: '/', name: 'activities', component: ActivitiesView },
    { path: '/activities/:id', name: 'activity-detail', component: ActivityDetailView },
    { path: '/login', name: 'login', component: LoginView, meta: { guest: true } },
    { path: '/register', name: 'register', component: RegisterView, meta: { guest: true } },
    {
      path: '/my-registrations',
      name: 'student-registrations',
      component: StudentRegistrationsView,
      meta: { role: 'STUDENT' },
    },
    {
      path: '/teacher/activities',
      name: 'teacher-activities',
      component: TeacherActivitiesView,
      meta: { role: 'TEACHER' },
    },
    {
      path: '/teacher/activities/new',
      name: 'activity-create',
      component: ActivityEditorView,
      meta: { role: 'TEACHER' },
    },
    {
      path: '/teacher/activities/:id/edit',
      name: 'activity-edit',
      component: ActivityEditorView,
      meta: { role: 'TEACHER' },
    },
    { path: '/:pathMatch(.*)*', component: NotFoundView },
  ],
})

router.beforeEach((to) => {
  if (to.meta.role && session.user?.role !== to.meta.role) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guest && session.user) return { name: 'activities' }
})

export default router
