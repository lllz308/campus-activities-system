<script setup>
defineProps({ status: { type: String, required: true } })

const labels = {
  DRAFT: '草稿',
  PUBLISHED: '报名中',
  CANCELLED: '已取消',
}
</script>

<template>
  <span :class="['status-badge', `status-${status.toLowerCase()}`]">{{ labels[status] || status }}</span>
</template>

