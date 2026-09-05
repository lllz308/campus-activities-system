<script setup>
import { CalendarX2 } from '@lucide/vue'

defineProps({ title: String, description: String })
</script>

<template>
  <div class="empty-state">
    <CalendarX2 :size="34" />
    <strong>{{ title }}</strong>
    <span>{{ description }}</span>
    <slot />
  </div>
</template>
