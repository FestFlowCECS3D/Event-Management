import { EventItem } from '../types';

export const initialEvents: EventItem[] = [
  {
    id: 'evt-001',
    title: 'Generative AI & LLM Systems Masterclass',
    subtitle: 'Hands-on transformer architectures, prompt engineering, and Gemini API integration.',
    type: 'workshop',
    category: 'Artificial Intelligence',
    // TODO: source PDF cut this sentence off mid-word — finish it before shipping
    description: 'Deep dive into state-of-the-art Generative AI pipelines and fine-tuning lightweight LLMs.',
    fullAgenda: [
      { time: '10:00 AM', activity: 'Intro to Transformer Neural Networks', speaker: 'Dr. Elena Rostova' },
      // TODO: speaker name was truncated in source PDF ("Dr. Elena Ros...") — confirm spelling
      { time: '11:15 AM', activity: 'Building Multimodal Agents with Gemini SDK', speaker: 'Dr. Elena Rostova' },
      { time: '12:30 PM', activity: 'Hands-on Hack Lab & Code Deployment', speaker: 'Dev Team' },
    ],
    date: 'AUG 14, 2026',
    startTime: '10:00 AM',
    endTime: '01:00 PM',
    dayNumber: 1,
    venue: 'Cyber Auditorium - Hall A',
    speaker: {
      name: 'Dr. Elena Rostova',
      role: 'Chief AI Scientist, DeepLab',
      // TODO: query string was cut off in source PDF, verify full Unsplash URL
      avatar: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80',
    },
    price: 0,
    totalSlots: 150,
    registeredSlots: 128,
    bannerGradient: 'from-cyan-500 via-blue-600 to-indigo-900',
    // TODO: query string was cut off in source PDF, verify full Unsplash URL
    bannerImage: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80',
    tags: ['AI', 'Python', 'Gemini', 'LLM'],
    prerequisites: ['Basic Python or TypeScript knowledge'],
    perks: ['Verified AI Certificate', 'Gemini API Credits', 'Swag Kit'],
    featured: true,
    isWorkshop: true,
  },
  {
    id: 'evt-002',
    title: 'Autonomous Robotics & Vision Workshop',
    subtitle: 'Program ROS 2 robots with computer vision and real-time obstacle avoidance.',
    type: 'workshop',
    category: 'Robotics',
    // TODO: source PDF cut this sentence off mid-word ("Raspbe...") — finish it before shipping
    description: 'Construct and program hardware vision systems using LiDAR and OpenCV on embedded Raspberry Pi devices.',
    fullAgenda: [
      { time: '02:00 PM', activity: 'LiDAR Mapping & Sensor Fusion', speaker: 'Prof. Kenji Sato' },
      { time: '03:30 PM', activity: 'Autonomous Navigation Circuit Challenge', speaker: 'Robotics Guild' },
    ],
    date: 'AUG 15, 2026',
    startTime: '02:00 PM',
    endTime: '05:00 PM',
    dayNumber: 2,
    venue: 'Mechatronics Innovation Arena',
    speaker: {
      name: 'Prof. Kenji Sato',
      role: 'Director, CyberRobotics Lab',
      // TODO: query string was cut off in source PDF, verify full Unsplash URL
      avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
    },
    price: 15,
    totalSlots: 80,
    registeredSlots: 74,
    bannerGradient: 'from-purple-600 via-pink-600 to-rose-900',
    // TODO: query string was cut off in source PDF, verify full Unsplash URL
    bannerImage: 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800&auto=format&fit=crop&q=80',
    tags: ['ROS2', 'Computer Vision', 'Embedded'],
    prerequisites: ['C++ or Python Basics'],
    perks: ['Hardware Micro-Controller Kit', 'Printed Workbook'],
    featured: true,
    isWorkshop: true,
  },
];
